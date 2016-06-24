package baram.view.cubeplot;

import java.awt.*;

public class SurfaceCanvas extends Canvas {

	private static final long serialVersionUID = 201404081840L;

	private ISurfacePlotModel model;
	private Image bufferImage;
	private Graphics bufferGraphics;
	private boolean image_drawn;
	private Projector projector;
	private Point3D vertex[];
	private Point3D modelVertex[];
	private boolean data_available;
	private boolean printing;
	private int curBufferWidth;
	private int curBufferHeight;
	private int printwidth;
	private int printheight;
	private float color;
	private Point3D cop;
	private int plot_mode;
	private int calc_divisions;
	private boolean isBoxed;
	private boolean isMesh;
	private boolean isScaleBox;
	private boolean isDisplayXY;
	private boolean isDisplayZ;
	private boolean isDisplayGrids;
	private float xmin;
	private float xmax;
	private float ymin;
	private float ymax;
	private float zmin;
	private float zmax;
	protected String xAxisLabel;
	protected String yAxisLabel;
	protected String zAxisLabel;
	private int click_x;
	private int click_y;
	private int factor_x;
	private int factor_y;
	private int t_x;
	private int t_y;
	private int t_z;
	private float color_factor;
	private Color line_color;
	private final int poly_x[] = new int[9];
	private final int poly_y[] = new int[9];

	public SurfaceCanvas() {
		xAxisLabel = "X";
		yAxisLabel = "Y";
		zAxisLabel = "Z";
		projector = new Projector();
		projector.setDistance(70F);
		projector.set2DScaling(15F);
		projector.setRotationAngle(125F);
		projector.setElevationAngle(10F);
		Point3D.setProjector(projector);
	}

	public void setModel(ISurfacePlotModel model) {
		this.model = model;
		plot_mode = model.getPlotMode();
		isBoxed = model.isBoxed();
		isMesh = model.isMesh();
		isScaleBox = model.isScaleBox();
		isDisplayXY = model.isDisplayXY();
		isDisplayZ = model.isDisplayZ();
		isDisplayGrids = model.isDisplayGrids();
		calc_divisions = model.getCalcDivisions();
		xAxisLabel = model.getXAxisLabel();
		yAxisLabel = model.getYAxisLabel();
		zAxisLabel = model.getZAxisLabel();
		bufferImage = null;
		bufferGraphics = null;
		image_drawn = false;
		data_available = false;
		printing = false;
		curBufferWidth = curBufferHeight = -1;
		if (bufferImage != null) {
			bufferImage.flush();
		}
		bufferImage = null;
		renderSurface();
		renderSurfaceModel();
	}

	public void destroyImage() {
		image_drawn = false;
	}

	public void setRanges(float xmin, float xmax, float ymin, float ymax) {
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
	}

	public float[] getRanges() {
		float ranges[] = new float[6];
		ranges[0] = xmin;
		ranges[1] = xmax;
		ranges[2] = ymin;
		ranges[3] = ymax;
		ranges[4] = zmin;
		ranges[5] = zmax;
		return ranges;
	}

	public void setDataAvailability(boolean avail) {
		data_available = avail;
	}

	public void setValuesArray(Point3D vertex[]) {
		this.vertex = vertex;
	}

	public void setValuesModelArray(Point3D vertex[]) {
		this.modelVertex = vertex;
	}

	public Point3D[] getValuesArray() {
		if (!data_available)
			return null;
		else
			return vertex;
	}

	public Point3D[] getValuesModelArray() {
		if (!data_available)
			return null;
		else
			return modelVertex;
	}

	public boolean mouseDown(Event e, int x, int y) {
		click_x = x;
		click_y = y;
		return true;
	}

	public boolean mouseDrag(Event e, int x, int y) {
		float new_value = 0.0F;
		if (e.controlDown()) {
			projector.set2D_xTranslation(projector.get2D_xTranslation() + (x - click_x));
			projector.set2D_yTranslation(projector.get2D_yTranslation() + (y - click_y));
		} else if (e.shiftDown()) {
			new_value = projector.get2DScaling() + (float) (y - click_y) * 0.5F;
			if (new_value > 60F)
				new_value = 60F;
			if (new_value < 2.0F)
				new_value = 2.0F;
			projector.set2DScaling(new_value);
		} else {
			for (new_value = projector.getRotationAngle() + (float) (x - click_x); new_value > 360F; new_value -= 360F)
				;
			for (; new_value < 0.0F; new_value += 360F)
				;
			projector.setRotationAngle(new_value);
			new_value = projector.getElevationAngle() + (float) (y - click_y);
			if (new_value > 90F)
				new_value = 90F;
			else if (new_value < 0.0F)
				new_value = 0.0F;
			projector.setElevationAngle(new_value);
		}
		image_drawn = false;
		repaint();
		click_x = x;
		click_y = y;
		return true;
	}

	private void renderSurfaceModel() {
		float xi = model.getXMin();
		float yi = model.getYMin();
		float xx = model.getXMax();
		float yx = model.getYMax();
		setRanges(xi, xx, yi, yx);
		calc_divisions = model.getCalcDivisions();
		setDataAvailability(false);
		float stepx = (xx - xi) / (float) calc_divisions;
		float stepy = (yx - yi) / (float) calc_divisions;
		int total = (calc_divisions + 1) * (calc_divisions + 1);
		Point3D tmpVertices[] = new Point3D[total];
		float max = (0.0F / 0.0F);
		float min = (0.0F / 0.0F);
		destroyImage();
		int i = 0;
		int j = 0;
		int k = 0;
		float x = xi;
		float y = yi;
		float xfactor = 20F / (xx - xi);
		float yfactor = 20F / (yx - yi);
		while (i <= calc_divisions) {
			while (j <= calc_divisions) {
				float v = model.calculateSubZ(x, y);
				if (Float.isInfinite(v))
					v = (0.0F / 0.0F);
				if (!Float.isNaN(v))
					if (Float.isNaN(max) || v > max)
						max = v;
					else if (Float.isNaN(min) || v < min)
						min = v;
				tmpVertices[k] = new Point3D((x - xi) * xfactor - 10F, (y - yi) * yfactor - 10F, v);
				j++;
				y += stepy;
				k++;
			}
			j = 0;
			y = yi;
			i++;
			x += stepx;
		}
		setValuesModelArray(tmpVertices);
		setDataAvailability(true);
		repaint();
	}

	private void renderSurface() {
		float xi = model.getXMin();
		float yi = model.getYMin();
		float xx = model.getXMax();
		float yx = model.getYMax();
		setRanges(xi, xx, yi, yx);
		calc_divisions = model.getCalcDivisions();
		setDataAvailability(false);
		float stepx = (xx - xi) / (float) calc_divisions;
		float stepy = (yx - yi) / (float) calc_divisions;
		int total = (calc_divisions + 1) * (calc_divisions + 1);
		Point3D tmpVertices[] = new Point3D[total];
		float max = (0.0F / 0.0F);
		float min = (0.0F / 0.0F);
		destroyImage();
		int i = 0;
		int j = 0;
		int k = 0;
		float x = xi;
		float y = yi;
		float xfactor = 20F / (xx - xi);
		float yfactor = 20F / (yx - yi);
		while (i <= calc_divisions) {
			while (j <= calc_divisions) {
				float v = model.calculateMainZ(x, y);
				if (Float.isInfinite(v))
					v = (0.0F / 0.0F);
				if (!Float.isNaN(v))
					if (Float.isNaN(max) || v > max)
						max = v;
					else if (Float.isNaN(min) || v < min)
						min = v;
				tmpVertices[k] = new Point3D((x - xi) * xfactor - 10F, (y - yi) * yfactor - 10F, v);
				j++;
				y += stepy;
				k++;
			}
			j = 0;
			y = yi;
			i++;
			x += stepx;
		}
		setValuesArray(tmpVertices);
		setDataAvailability(true);
		repaint();
	}

	public void paint(Graphics g) {
		if (getBounds().width <= 0 || getBounds().height <= 0)
			return;
		if (bufferImage == null || getBounds().width != curBufferWidth || getBounds().height != curBufferHeight) {
			projector.setProjectionArea(new Rectangle(0, 0, getBounds().width, getBounds().height));
			image_drawn = false;
			if (bufferImage != null)
				bufferImage.flush();
			bufferImage = createImage(getBounds().width, getBounds().height);
			if (bufferGraphics != null)
				bufferGraphics.dispose();
			bufferGraphics = bufferImage.getGraphics();
			curBufferWidth = getBounds().width;
			curBufferHeight = getBounds().height;
		}
		printing = g instanceof PrintGraphics;
		if (printing) {
			Graphics savedgc = bufferGraphics;
			bufferGraphics = g;
			Dimension pagedimension = ((PrintGraphics) g).getPrintJob().getPageDimension();
			printwidth = pagedimension.width;
			printheight = (curBufferHeight * printwidth) / curBufferWidth;
			if (printheight > pagedimension.height) {
				printheight = pagedimension.height;
				printwidth = (curBufferWidth * printheight) / curBufferHeight;
			}
			float savedscalingfactor = projector.get2DScaling();
			projector.setProjectionArea(new Rectangle(0, 0, printwidth, printheight));
			projector.set2DScaling((savedscalingfactor * (float) printwidth) / (float) curBufferWidth);
			bufferGraphics.clipRect(0, 0, printwidth, printheight);
			if (!data_available) {
				drawBoxGridsTicksLabels(bufferGraphics);
			} else {
				int fontsize = (int) Math.round((double) projector.get2DScaling() * 0.80000000000000004D);
				bufferGraphics.setFont(new Font("Arial", 0, fontsize));
				Point3D.invalidate();
				plotSurface();
				if (isBoxed)
					drawBoundingBox();
			}
			bufferGraphics.drawRect(0, 0, printwidth - 1, printheight - 1);
			projector.set2DScaling(savedscalingfactor);
			projector.setProjectionArea(new Rectangle(0, 0, getBounds().width, getBounds().height));
			bufferGraphics = savedgc;
		} else if (image_drawn && bufferImage != null) {
			g.drawImage(bufferImage, 0, 0, this);
		} else if (data_available) {
			int fontsize = (int) Math.round((double) projector.get2DScaling() * 0.80000000000000004D);
			bufferGraphics.setFont(new Font("Arial", 0, fontsize));
			Point3D.invalidate();
			plotSurface();
			if (isBoxed)
				drawBoundingBox();
			image_drawn = true;
			g.drawImage(bufferImage, 0, 0, this);
		} else {
			g.setColor(Color.gray);
			g.fillRect(0, 0, getBounds().width, getBounds().height);
			drawBoxGridsTicksLabels(g);
		}
	}

	public void update(Graphics g) {
		paint(g);
	}

	private void drawBoundingBox() {
		Point startingpoint = projector.project(factor_x * 10, factor_y * 10, 10F);
		// bufferGraphics.setColor(Color.black);
		bufferGraphics.setColor(Color.gray);
		Point projection = projector.project(-factor_x * 10, factor_y * 10, 10F);
		bufferGraphics.drawLine(startingpoint.x, startingpoint.y, projection.x, projection.y);
		projection = projector.project(factor_x * 10, -factor_y * 10, 10F);
		bufferGraphics.drawLine(startingpoint.x, startingpoint.y, projection.x, projection.y);
		projection = projector.project(factor_x * 10, factor_y * 10, -10F);
		bufferGraphics.drawLine(startingpoint.x, startingpoint.y, projection.x, projection.y);
	}

	private void drawBase(Graphics g, int x[], int y[]) {
		Point projection = projector.project(-10F, -10F, -10F);
		x[0] = projection.x;
		y[0] = projection.y;
		projection = projector.project(-10F, 10F, -10F);
		x[1] = projection.x;
		y[1] = projection.y;
		projection = projector.project(10F, 10F, -10F);
		x[2] = projection.x;
		y[2] = projection.y;
		projection = projector.project(10F, -10F, -10F);
		x[3] = projection.x;
		y[3] = projection.y;
		x[4] = x[0];
		y[4] = y[0];
		if (plot_mode != 0) {
			if (plot_mode == 1)
				g.setColor(Color.lightGray);
			else
				g.setColor(Color.white);
			// g.setColor(new Color(192, 220, 192));
			g.fillPolygon(x, y, 4);
		}
		// g.setColor(Color.black);
		g.setColor(Color.gray);
		g.drawPolygon(x, y, 5);
	}

	private void drawBoxGridsTicksLabels(Graphics g) {
		boolean x_left = false;
		boolean y_left = false;
		int x[] = new int[5];
		int y[] = new int[5];
		if (projector == null)
			return;
		factor_x = factor_y = 1;
		Point projection = projector.project(0.0F, 0.0F, -10F);
		x[0] = projection.x;
		projection = projector.project(10.5F, 0.0F, -10F);
		y_left = projection.x > x[0];
		int i = projection.y;
		projection = projector.project(-10.5F, 0.0F, -10F);
		if (projection.y > i) {
			factor_x = -1;
			y_left = projection.x > x[0];
		}
		projection = projector.project(0.0F, 10.5F, -10F);
		x_left = projection.x > x[0];
		i = projection.y;
		projection = projector.project(0.0F, -10.5F, -10F);
		if (projection.y > i) {
			factor_y = -1;
			x_left = projection.x > x[0];
		}
		setAxesScale();
		drawBase(g, x, y);
		if (isBoxed) {
			projection = projector.project(-factor_x * 10, -factor_y * 10, -10F);
			x[0] = projection.x;
			y[0] = projection.y;
			projection = projector.project(-factor_x * 10, -factor_y * 10, 10F);
			x[1] = projection.x;
			y[1] = projection.y;
			projection = projector.project(factor_x * 10, -factor_y * 10, 10F);
			x[2] = projection.x;
			y[2] = projection.y;
			projection = projector.project(factor_x * 10, -factor_y * 10, -10F);
			x[3] = projection.x;
			y[3] = projection.y;
			x[4] = x[0];
			y[4] = y[0];
			if (plot_mode != 0) {
				if (plot_mode == 1)
					g.setColor(Color.lightGray);
				else
					g.setColor(Color.white);
				// g.setColor(new Color(192, 220, 192));
				g.fillPolygon(x, y, 4);
			}
			// g.setColor(Color.black);
			g.setColor(Color.gray);
			g.drawPolygon(x, y, 5);
			projection = projector.project(-factor_x * 10, factor_y * 10, 10F);
			x[2] = projection.x;
			y[2] = projection.y;
			projection = projector.project(-factor_x * 10, factor_y * 10, -10F);
			x[3] = projection.x;
			y[3] = projection.y;
			x[4] = x[0];
			y[4] = y[0];
			if (plot_mode != 0) {
				if (plot_mode == 1)
					g.setColor(Color.lightGray);
				else
					g.setColor(Color.white);
				// g.setColor(new Color(192, 220, 192));
				g.fillPolygon(x, y, 4);
			}
			// g.setColor(Color.black);
			g.setColor(Color.gray);
			g.drawPolygon(x, y, 5);
		} else if (isDisplayZ) {
			projection = projector.project(factor_x * 10, -factor_y * 10, -10F);
			x[0] = projection.x;
			y[0] = projection.y;
			projection = projector.project(factor_x * 10, -factor_y * 10, 10F);
			g.drawLine(x[0], y[0], projection.x, projection.y);
			projection = projector.project(-factor_x * 10, factor_y * 10, -10F);
			x[0] = projection.x;
			y[0] = projection.y;
			projection = projector.project(-factor_x * 10, factor_y * 10, 10F);
			g.drawLine(x[0], y[0], projection.x, projection.y);
		}
		for (i = -9; i <= 9; i++) {
			Point tickpos;
			if (isDisplayXY || isDisplayGrids) {
				if (!isDisplayGrids || i % (t_y / 2) == 0 || isDisplayXY) {
					if (isDisplayGrids && i % t_y == 0)
						projection = projector.project(-factor_x * 10, i, -10F);
					else if (i % t_y != 0)
						projection = projector.project((float) factor_x * 9.8F, i, -10F);
					else
						projection = projector.project((float) factor_x * 9.5F, i, -10F);
					tickpos = projector.project(factor_x * 10, i, -10F);
					g.drawLine(projection.x, projection.y, tickpos.x, tickpos.y);
					if (i % t_y == 0 && isDisplayXY) {
						tickpos = projector.project((float) factor_x * 10.5F, i, -10F);
						if (y_left)
							drawNumber(g, tickpos.x, tickpos.y,
									(float) (((double) (i + 10) / 20D) * (double) (ymax - ymin) + (double) ymin), 0, 0);
						else
							drawNumber(g, tickpos.x, tickpos.y,
									(float) (((double) (i + 10) / 20D) * (double) (ymax - ymin) + (double) ymin), 2, 0);
					}
				}
				if (!isDisplayGrids || i % (t_x / 2) == 0 || isDisplayXY) {
					if (isDisplayGrids && i % t_x == 0)
						projection = projector.project(i, -factor_y * 10, -10F);
					else if (i % t_x != 0)
						projection = projector.project(i, (float) factor_y * 9.8F, -10F);
					else
						projection = projector.project(i, (float) factor_y * 9.5F, -10F);
					tickpos = projector.project(i, factor_y * 10, -10F);
					g.drawLine(projection.x, projection.y, tickpos.x, tickpos.y);
					if (i % t_x == 0 && isDisplayXY) {
						tickpos = projector.project(i, (float) factor_y * 10.5F, -10F);
						if (x_left)
							drawNumber(g, tickpos.x, tickpos.y,
									(float) (((double) (i + 10) / 20D) * (double) (xmax - xmin) + (double) xmin), 0, 0);
						else
							drawNumber(g, tickpos.x, tickpos.y,
									(float) (((double) (i + 10) / 20D) * (double) (xmax - xmin) + (double) xmin), 2, 0);
					}
				}
			}
			if (!isDisplayZ && (!isDisplayGrids || !isBoxed) || isDisplayGrids && i % (t_z / 2) != 0 && !isDisplayZ)
				continue;
			if (isBoxed && isDisplayGrids && i % t_z == 0) {
				projection = projector.project(-factor_x * 10, -factor_y * 10, i);
				tickpos = projector.project(-factor_x * 10, factor_y * 10, i);
			} else {
				if (i % t_z == 0)
					projection = projector.project(-factor_x * 10, (float) factor_y * 9.5F, i);
				else
					projection = projector.project(-factor_x * 10, (float) factor_y * 9.8F, i);
				tickpos = projector.project(-factor_x * 10, factor_y * 10, i);
			}
			g.drawLine(projection.x, projection.y, tickpos.x, tickpos.y);
			if (isDisplayZ) {
				tickpos = projector.project(-factor_x * 10, (float) factor_y * 10.5F, i);
				if (i % t_z == 0)
					if (x_left)
						drawNumber(g, tickpos.x, tickpos.y,
								(float) (((double) (i + 10) / 20D) * (double) (zmax - zmin) + (double) zmin), 0, 1);
					else
						drawNumber(g, tickpos.x, tickpos.y,
								(float) (((double) (i + 10) / 20D) * (double) (zmax - zmin) + (double) zmin), 2, 1);
			}
			if (isDisplayGrids && isBoxed && i % t_z == 0) {
				projection = projector.project(-factor_x * 10, -factor_y * 10, i);
				tickpos = projector.project(factor_x * 10, -factor_y * 10, i);
			} else {
				if (i % t_z == 0)
					projection = projector.project((float) factor_x * 9.5F, -factor_y * 10, i);
				else
					projection = projector.project((float) factor_x * 9.8F, -factor_y * 10, i);
				tickpos = projector.project(factor_x * 10, -factor_y * 10, i);
			}
			g.drawLine(projection.x, projection.y, tickpos.x, tickpos.y);
			if (isDisplayZ) {
				tickpos = projector.project((float) factor_x * 10.5F, -factor_y * 10, i);
				if (i % t_z == 0)
					if (y_left)
						drawNumber(g, tickpos.x, tickpos.y,
								(float) (((double) (i + 10) / 20D) * (double) (zmax - zmin) + (double) zmin), 0, 1);
					else
						drawNumber(g, tickpos.x, tickpos.y,
								(float) (((double) (i + 10) / 20D) * (double) (zmax - zmin) + (double) zmin), 2, 1);
			}
			if (!isDisplayGrids || !isBoxed)
				continue;
			if (i % t_y == 0) {
				projection = projector.project(-factor_x * 10, i, -10F);
				tickpos = projector.project(-factor_x * 10, i, 10F);
				g.drawLine(projection.x, projection.y, tickpos.x, tickpos.y);
			}
			if (i % t_x == 0) {
				projection = projector.project(i, -factor_y * 10, -10F);
				tickpos = projector.project(i, -factor_y * 10, 10F);
				g.drawLine(projection.x, projection.y, tickpos.x, tickpos.y);
			}
		}

		if (isDisplayXY) {
			Point tickpos = projector.project(0.0F, factor_y * 14, -10F);
			drawString(g, tickpos.x, tickpos.y, xAxisLabel, 1, 0);
			tickpos = projector.project(factor_x * 14, 0.0F, -10F);
			drawString(g, tickpos.x, tickpos.y, yAxisLabel, 1, 0);
		}
		if (isDisplayZ) {
			Point tickpos = projector.project(-factor_x * 10, factor_y * 14, 0.0F);
			drawString(g, tickpos.x, tickpos.y, zAxisLabel, 1, 0);
		}
	}

	private void setAxesScale() {
		if (!isScaleBox) {
			projector.setScaling(1.0F);
			t_x = t_y = t_z = 4;
			return;
		}
		float scale_x = xmax - xmin;
		float scale_y = ymax - ymin;
		float scale_z = zmax - zmin;
		float divisor;
		int longest;
		if (scale_x < scale_y) {
			if (scale_y < scale_z) {
				longest = 3;
				divisor = scale_z;
			} else {
				longest = 2;
				divisor = scale_y;
			}
		} else if (scale_x < scale_z) {
			longest = 3;
			divisor = scale_z;
		} else {
			longest = 1;
			divisor = scale_x;
		}
		scale_x /= divisor;
		scale_y /= divisor;
		scale_z /= divisor;
		if (scale_x < 0.2F || scale_y < 0.2F && scale_z < 0.2F)
			switch (longest) {
			default:
				break;

			case 1: // '\001'
				if (scale_y < scale_z) {
					scale_y /= scale_z;
					scale_z = 1.0F;
				} else {
					scale_z /= scale_y;
					scale_y = 1.0F;
				}
				break;

			case 2: // '\002'
				if (scale_x < scale_z) {
					scale_x /= scale_z;
					scale_z = 1.0F;
				} else {
					scale_z /= scale_x;
					scale_x = 1.0F;
				}
				break;

			case 3: // '\003'
				if (scale_y < scale_x) {
					scale_y /= scale_x;
					scale_x = 1.0F;
				} else {
					scale_x /= scale_y;
					scale_y = 1.0F;
				}
				break;
			}
		if (scale_x < 0.2F)
			scale_x = 1.0F;
		projector.setXScaling(scale_x);
		if (scale_y < 0.2F)
			scale_y = 1.0F;
		projector.setYScaling(scale_y);
		if (scale_z < 0.2F)
			scale_z = 1.0F;
		projector.setZScaling(scale_z);
		if (scale_x < 0.5F)
			t_x = 8;
		else
			t_x = 4;
		if (scale_y < 0.5F)
			t_y = 8;
		else
			t_y = 4;
		if (scale_z < 0.5F)
			t_z = 8;
		else
			t_z = 4;
	}

	private void drawString(Graphics g, int x, int y, String s, int x_align, int y_align) {
		switch (y_align) {
		case 0: // '\0'
			y += g.getFontMetrics(g.getFont()).getAscent();
			break;

		case 1: // '\001'
			y += g.getFontMetrics(g.getFont()).getAscent() / 2;
			break;
		}
		switch (x_align) {
		case 0: // '\0'
			g.drawString(s, x, y);
			break;

		case 2: // '\002'
			g.drawString(s, x - g.getFontMetrics(g.getFont()).stringWidth(s), y);
			break;

		case 1: // '\001'
			g.drawString(s, x - g.getFontMetrics(g.getFont()).stringWidth(s) / 2, y);
			break;
		}
	}

	private void drawNumber(Graphics g, int x, int y, float f, int x_align, int y_align) {
		String s = Float.toString(f);
		drawString(g, x, y, s, x_align, y_align);
	}

	private void plotPlane(Point3D vertex[], int verticescount, boolean isModel) {
		if (verticescount < 3)
			return;
		int count = 0;
		float z = 0.0F;
		line_color = Color.darkGray;
		boolean low1 = vertex[0].z < zmin;
		boolean valid1 = !low1 && vertex[0].z <= zmax;
		int index = 1;
		for (int loop = 0; loop < verticescount; loop++) {
			boolean low2 = vertex[index].z < zmin;
			boolean valid2 = !low2 && vertex[index].z <= zmax;
			if (valid1 || valid2 || low1 ^ low2) {
				if (!valid1) {
					float result;
					if (low1)
						result = zmin;
					else
						result = zmax;
					float ratio = (result - vertex[index].z) / (vertex[loop].z - vertex[index].z);
					float new_x = ratio * (vertex[loop].x - vertex[index].x) + vertex[index].x;
					float new_y = ratio * (vertex[loop].y - vertex[index].y) + vertex[index].y;
					Point projection;
					if (low1)
						projection = projector.project(new_x, new_y, -10F);
					else
						projection = projector.project(new_x, new_y, 10F);
					poly_x[count] = projection.x;
					poly_y[count] = projection.y;
					count++;
					z += result;
				}
				if (valid2) {
					Point projection = vertex[index].projection();
					poly_x[count] = projection.x;
					poly_y[count] = projection.y;
					count++;
					z += vertex[index].z;
				} else {
					float result;
					if (low2)
						result = zmin;
					else
						result = zmax;
					float ratio = (result - vertex[loop].z) / (vertex[index].z - vertex[loop].z);
					float new_x = ratio * (vertex[index].x - vertex[loop].x) + vertex[loop].x;
					float new_y = ratio * (vertex[index].y - vertex[loop].y) + vertex[loop].y;
					Point projection;
					if (low2)
						projection = projector.project(new_x, new_y, -10F);
					else
						projection = projector.project(new_x, new_y, 10F);
					poly_x[count] = projection.x;
					poly_y[count] = projection.y;
					count++;
					z += result;
				}
			}
			if (++index == verticescount)
				index = 0;
			valid1 = valid2;
			low1 = low2;
		}

		if (count > 0) {
			switch (plot_mode) {
			default:
				break;

			case 1: // '\001'
				bufferGraphics.setColor(Color.lightGray);
				break;

			case 2: // '\002'
				z = 0.8F - (z / (float) count - zmin) * color_factor;
				if (isModel) {
					bufferGraphics.setColor(new Color(192, 192, 192, 128));
				} else {
					bufferGraphics.setColor(new Color((int)((1.0f-z)*256), (int)(z*256), 0, (int)((1.0f-z)*256)));
					//bufferGraphics.setColor(Color.getHSBColor(1.0f, 1.0f, 1.0f));
				}
				break;

			case 3: // '\003'
				z = (z / (float) count - zmin) * color_factor;
				bufferGraphics.setColor(Color.getHSBColor(0.0F, 0.0F, z));
				if (z < 0.3F)
					line_color = new Color(0.6F, 0.6F, 0.6F);
				break;

			case 4: // '\004'
				z = (z / (float) count - zmin) * color_factor + 0.4F;
				if (isModel) {
					bufferGraphics.setColor(new Color(192f, 192f, 192f, 0.5f));
				} else {
					bufferGraphics.setColor(Color.getHSBColor(color, 0.7F, z));
				}
				break;
			}
			bufferGraphics.fillPolygon(poly_x, poly_y, count);
			bufferGraphics.setColor(line_color);
			if (isMesh || plot_mode == 1) {
				poly_x[count] = poly_x[0];
				poly_y[count] = poly_y[0];
				count++;
				bufferGraphics.drawPolygon(poly_x, poly_y, count);
			}
		}
	}

	private static boolean isPointsValid(Point3D values[]) {
		return !values[0].isInvalid() && !values[1].isInvalid() && !values[2].isInvalid() && !values[3].isInvalid();
	}

	private void plotArea(int start_lx, int start_ly, int end_lx, int end_ly, int sx, int sy) {
		Point3D values1[] = new Point3D[4];
		start_lx *= calc_divisions + 1;
		sx *= calc_divisions + 1;
		end_lx *= calc_divisions + 1;
		int lx = start_lx;
		for (int ly = start_ly; ly != end_ly;) {
			values1[1] = vertex[lx + ly];
			values1[2] = vertex[lx + ly + sy];
			for (; lx != end_lx; lx += sx) {
				values1[0] = values1[1];
				values1[1] = vertex[lx + sx + ly];
				values1[3] = values1[2];
				values1[2] = vertex[lx + sx + ly + sy];
				if (plot_mode == 4)
					color = 0.2F;
				if (isPointsValid(values1))
					plotPlane(values1, 4, false);
			}

			ly += sy;
			lx = start_lx;
		}

	}

	private void plotModelArea(int start_lx, int start_ly, int end_lx, int end_ly, int sx, int sy) {
		Point3D values1[] = new Point3D[4];
		start_lx *= calc_divisions + 1;
		sx *= calc_divisions + 1;
		end_lx *= calc_divisions + 1;
		int lx = start_lx;
		for (int ly = start_ly; ly != end_ly;) {
			values1[1] = modelVertex[lx + ly];
			values1[2] = modelVertex[lx + ly + sy];
			for (; lx != end_lx; lx += sx) {
				values1[0] = values1[1];
				values1[1] = modelVertex[lx + sx + ly];
				values1[3] = values1[2];
				values1[2] = modelVertex[lx + sx + ly + sy];
				if (plot_mode == 4)
					color = 0.2F;
				if (isPointsValid(values1))
					plotPlane(values1, 4, true);
			}

			ly += sy;
			lx = start_lx;
		}

	}

	private void plotSurface() {
		image_drawn = false;
		float zi = model.getZMin();
		float zx = model.getZMax();
		int plot_density = model.getDispDivisions();
		int multiple_factor = calc_divisions / plot_density;
		zmin = zi;
		zmax = zx;
		color_factor = 0.8F / (zmax - zmin);
		if (plot_mode == 4)
			color_factor *= 0.75F;
		if (!printing) {
			// bufferGraphics.setColor(Color.lightGray);
			bufferGraphics.setColor(Color.white);
			bufferGraphics.fillRect(0, 0, getBounds().width, getBounds().height);
		}
		drawBoxGridsTicksLabels(bufferGraphics);
		Point3D.setZRange(zmin, zmax);
		float distance = projector.getDistance() * projector.getCosElevationAngle();
		cop = new Point3D(distance * projector.getSinRotationAngle(), distance * projector.getCosRotationAngle(),
				projector.getDistance() * projector.getSinElevationAngle());
		cop.transform();
		boolean inc_x = cop.x > 0.0F;
		boolean inc_y = cop.y > 0.0F;
		int sx;
		int start_lx;
		int end_lx;
		if (inc_x) {
			start_lx = 0;
			end_lx = calc_divisions;
			sx = multiple_factor;
		} else {
			start_lx = calc_divisions;
			end_lx = 0;
			sx = -multiple_factor;
		}
		int sy;
		int start_ly;
		int end_ly;
		if (inc_y) {
			start_ly = 0;
			end_ly = calc_divisions;
			sy = multiple_factor;
		} else {
			start_ly = calc_divisions;
			end_ly = 0;
			sy = -multiple_factor;
		}
		// Plot Area
		if (cop.x > 10F || cop.x < -10F) {
			if (cop.y > 10F || cop.y < -10F) {
				plotArea(start_lx, start_ly, end_lx, end_ly, sx, sy);
			} else {
				int split_y = (int) (((cop.y + 10F) * (float) plot_density) / 20F) * multiple_factor;
				plotArea(start_lx, 0, end_lx, split_y, sx, multiple_factor);
				plotArea(start_lx, calc_divisions, end_lx, split_y, sx, -multiple_factor);
			}
		} else if (cop.y > 10F || cop.y < -10F) {
			int split_x = (int) (((cop.x + 10F) * (float) plot_density) / 20F) * multiple_factor;
			plotArea(0, start_ly, split_x, end_ly, multiple_factor, sy);
			plotArea(calc_divisions, start_ly, split_x, end_ly, -multiple_factor, sy);
		} else {
			int split_x = (int) (((cop.x + 10F) * (float) plot_density) / 20F) * multiple_factor;
			int split_y = (int) (((cop.y + 10F) * (float) plot_density) / 20F) * multiple_factor;
			plotArea(0, 0, split_x, split_y, multiple_factor, multiple_factor);
			plotArea(0, calc_divisions, split_x, split_y, multiple_factor, -multiple_factor);
			plotArea(calc_divisions, 0, split_x, split_y, -multiple_factor, multiple_factor);
			plotArea(calc_divisions, calc_divisions, split_x, split_y, -multiple_factor, -multiple_factor);
		}
		// Plot Model Area
		if (cop.x > 10F || cop.x < -10F) {
			if (cop.y > 10F || cop.y < -10F) {
				plotModelArea(start_lx, start_ly, end_lx, end_ly, sx, sy);
			} else {
				int split_y = (int) (((cop.y + 10F) * (float) plot_density) / 20F) * multiple_factor;
				plotModelArea(start_lx, 0, end_lx, split_y, sx, multiple_factor);
				plotModelArea(start_lx, calc_divisions, end_lx, split_y, sx, -multiple_factor);
			}
		} else if (cop.y > 10F || cop.y < -10F) {
			int split_x = (int) (((cop.x + 10F) * (float) plot_density) / 20F) * multiple_factor;
			plotModelArea(0, start_ly, split_x, end_ly, multiple_factor, sy);
			plotModelArea(calc_divisions, start_ly, split_x, end_ly, -multiple_factor, sy);
		} else {
			int split_x = (int) (((cop.x + 10F) * (float) plot_density) / 20F) * multiple_factor;
			int split_y = (int) (((cop.y + 10F) * (float) plot_density) / 20F) * multiple_factor;
			plotModelArea(0, 0, split_x, split_y, multiple_factor, multiple_factor);
			plotModelArea(0, calc_divisions, split_x, split_y, multiple_factor, -multiple_factor);
			plotModelArea(calc_divisions, 0, split_x, split_y, -multiple_factor, multiple_factor);
			plotModelArea(calc_divisions, calc_divisions, split_x, split_y, -multiple_factor, -multiple_factor);
		}
	}
}
