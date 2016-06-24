package baram.view.cubeplot;

import java.awt.Point;

public final class Point3D {

	private Point projection;
    private int project_index;
    private static Projector projector;
    private static float zmin;
    private static float zmax;
    private static float zfactor;
    private static int master_project_index = 0;
    public float x;
    public float y;
    public float z;
    
    Point3D (float ix, float iy, float iz) {
        x = ix;
        y = iy;
        z = iz;
        project_index = master_project_index - 1;
    }

    public final boolean isInvalid() {
        return Float.isNaN(z);
    }

    public final Point projection() {
        if (project_index != master_project_index) {
            projection = projector.project(x, y, (z - zmin) * zfactor - 10F);
            project_index = master_project_index;
        }
        return projection;
    }

    public final void transform() {
        x = x / projector.getXScaling();
        y = y / projector.getYScaling();
        z = ((zmax - zmin) * (z / projector.getZScaling() + 10F)) / 20F + zmin;
    }

    public static void invalidate() {
        master_project_index++;
    }

    public static void setProjector(Projector projector) {
    	Point3D.projector = projector;
    }

    public static void setZRange(float zmin, float zmax) {
    	Point3D.zmin = zmin;
    	Point3D.zmax = zmax;
        zfactor = 20F / (zmax - zmin);
    }
}
