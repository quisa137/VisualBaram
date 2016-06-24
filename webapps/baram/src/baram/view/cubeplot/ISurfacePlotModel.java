package baram.view.cubeplot;


public interface ISurfacePlotModel {

	public static final int PLOT_MODE_WIREFRAME = 0;
    public static final int PLOT_MODE_NORENDER = 1;
    public static final int PLOT_MODE_SPECTRUM = 2;
    public static final int PLOT_MODE_GRAYSCALE = 3;
    public static final int PLOT_MODE_DUALSHADE = 4;
    
    public abstract int getPlotMode();

    public abstract float calculateMainZ(float f, float f1);
    
    public abstract float calculateSubZ(float f, float f1);

    public abstract boolean isBoxed();

    public abstract boolean isMesh();

    public abstract boolean isScaleBox();

    public abstract boolean isDisplayXY();

    public abstract boolean isDisplayZ();

    public abstract boolean isDisplayGrids();

    public abstract int getCalcDivisions();

    public abstract int getDispDivisions();

    public abstract float getXMin();

    public abstract float getXMax();

    public abstract float getYMin();

    public abstract float getYMax();

    public abstract float getZMin();

    public abstract float getZMax();

    public abstract String getXAxisLabel();

    public abstract String getYAxisLabel();

    public abstract String getZAxisLabel();
}
