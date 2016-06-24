package baram.view.cubeplot;

public class SurfaceModel implements ISurfacePlotModel {
	
	String xLabel;
	String yLabel;
	String zLabel;
	
	public float calculateMainZ(float x, float y) {
		if(y>0) {
			return (float)((Math.exp(0.07*x)*(Math.log(y)+1))/(Math.log(x)+1)/Math.exp(0.07*y))*4.6f;
		} else {
			return 0.0f;
		}
	}
	
	public float calculateSubZ(float x, float y) {
		if(y>0) {
			//return (float)(Math.exp(0.07*x)*(Math.log(y)+1));
			return 0.0f;
		} else {
			return 0.0f;
		}
	}

	public int getPlotMode() {
		return ISurfacePlotModel.PLOT_MODE_SPECTRUM;
	}

	public boolean isBoxed() {
		return true;
	}

	public boolean isMesh() {
		return false;
	}

	public boolean isScaleBox() {
		return false;
	}

	public boolean isDisplayXY() {
		return true;
	}

	public boolean isDisplayZ() {
		return true;
	}

	public boolean isDisplayGrids() {
		return true;
	}

	public int getCalcDivisions() {
		return 101;
	}

	public int getDispDivisions() {
		return 101;
	}

	public float getXMin() {
		return 1.0f;
	}

	public float getXMax() {
		return 101.0f;
	}

	public float getYMin() {
		return 1.0f;
	}

	public float getYMax() {
		return 101.0f;
	}

	public float getZMin()
	{
		return 0.0f;
	}

	public float getZMax() {
		return 1001.0f;
	}

	public void setXAxisLabel(String xLabel) {
		this.xLabel = xLabel;
	}

	public void setYAxisLabel(String yLabel) {
		this.yLabel = yLabel;
	}

	public void setZAxisLabel(String zLabel) {
		this.zLabel = zLabel;
	}
	
	public String getXAxisLabel() {
		return xLabel;
	}

	public String getYAxisLabel() {
		return yLabel;
	}

	public String getZAxisLabel() {
		return zLabel;
	}
}