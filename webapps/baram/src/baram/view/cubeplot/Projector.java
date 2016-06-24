package baram.view.cubeplot;

import java.awt.Point;
import java.awt.Rectangle;

public final class Projector {

	private float scale_x;
	private float scale_y;
	private float scale_z;
	private float distance;
	private float _2D_scale;
	private float rotation;
	private float elevation;
	private float sin_rotation;
	private float cos_rotation;
	private float sin_elevation;
	private float cos_elevation;
	private int _2D_trans_x;
	private int _2D_trans_y;
	private int x1;
	private int x2;
	private int y1;
	private int y2;
	private int center_x;
	private int center_y;
	private int trans_x;
	private int trans_y;
	private float factor;
	private float sx_cos;
	private float sy_cos;
	private float sz_cos;
	private float sx_sin;
	private float sy_sin;
	private float sz_sin;

	Projector() {
		setScaling(1.0F);
		setRotationAngle(0.0F);
		setElevationAngle(0.0F);
		setDistance(10F);
		set2DScaling(1.0F);
		set2DTranslation(0, 0);
	}

	public void setProjectionArea(Rectangle r) {
		x1 = r.x;
		x2 = x1 + r.width;
		y1 = r.y;
		y2 = y1 + r.height;
		center_x = (x1 + x2) / 2;
		center_y = (y1 + y2) / 2;
		trans_x = center_x + _2D_trans_x;
		trans_y = center_y + _2D_trans_y;
	}

	public void setRotationAngle(float angle) {
		rotation = angle;
		sin_rotation = (float) Math.sin(angle * 0.01745329F);
		cos_rotation = (float) Math.cos(angle * 0.01745329F);
		sx_cos = -scale_x * cos_rotation;
		sx_sin = -scale_x * sin_rotation;
		sy_cos = -scale_y * cos_rotation;
		sy_sin = scale_y * sin_rotation;
	}

	public float getRotationAngle() {
		return rotation;
	}

	public float getSinRotationAngle() {
		return sin_rotation;
	}

	public float getCosRotationAngle() {
		return cos_rotation;
	}

	public void setElevationAngle(float angle) {
		elevation = angle;
		sin_elevation = (float) Math.sin(angle * 0.01745329F);
		cos_elevation = (float) Math.cos(angle * 0.01745329F);
		sz_cos = scale_z * cos_elevation;
		sz_sin = scale_z * sin_elevation;
	}

	public float getElevationAngle() {
		return elevation;
	}

	public float getSinElevationAngle() {
		return sin_elevation;
	}

	public float getCosElevationAngle() {
		return cos_elevation;
	}

	public void setDistance(float new_distance) {
		distance = new_distance;
		factor = distance * _2D_scale;
	}

	public float getDistance() {
		return distance;
	}

	public void setXScaling(float scaling) {
		scale_x = scaling;
		sx_cos = -scale_x * cos_rotation;
		sx_sin = -scale_x * sin_rotation;
	}

	public float getXScaling() {
		return scale_x;
	}

	public void setYScaling(float scaling) {
		scale_y = scaling;
		sy_cos = -scale_y * cos_rotation;
		sy_sin = scale_y * sin_rotation;
	}

	public float getYScaling() {
		return scale_y;
	}

	public void setZScaling(float scaling) {
		scale_z = scaling;
		sz_cos = scale_z * cos_elevation;
		sz_sin = scale_z * sin_elevation;
	}

	public float getZScaling() {
		return scale_z;
	}

	public void setScaling(float x, float y, float z) {
		scale_x = x;
		scale_y = y;
		scale_z = z;
		sx_cos = -scale_x * cos_rotation;
		sx_sin = -scale_x * sin_rotation;
		sy_cos = -scale_y * cos_rotation;
		sy_sin = scale_y * sin_rotation;
		sz_cos = scale_z * cos_elevation;
		sz_sin = scale_z * sin_elevation;
	}

	public void setScaling(float scaling) {
		scale_x = scale_y = scale_z = scaling;
		sx_cos = -scale_x * cos_rotation;
		sx_sin = -scale_x * sin_rotation;
		sy_cos = -scale_y * cos_rotation;
		sy_sin = scale_y * sin_rotation;
		sz_cos = scale_z * cos_elevation;
		sz_sin = scale_z * sin_elevation;
	}

	public void set2DScaling(float scaling) {
		_2D_scale = scaling;
		factor = distance * _2D_scale;
	}

	public float get2DScaling() {
		return _2D_scale;
	}

	public void set2DTranslation(int x, int y) {
		_2D_trans_x = x;
		_2D_trans_y = y;
		trans_x = center_x + _2D_trans_x;
		trans_y = center_y + _2D_trans_y;
	}

	public void set2D_xTranslation(int x) {
		_2D_trans_x = x;
		trans_x = center_x + _2D_trans_x;
	}

	public int get2D_xTranslation() {
		return _2D_trans_x;
	}

	public void set2D_yTranslation(int y) {
		_2D_trans_y = y;
		trans_y = center_y + _2D_trans_y;
	}

	public int get2D_yTranslation() {
		return _2D_trans_y;
	}

	public final Point project(float x, float y, float z) {
		float temp = x;
		x = x * sx_cos + y * sy_sin;
		y = temp * sx_sin + y * sy_cos;
		temp = factor / ((y * cos_elevation - z * sz_sin) + distance);
		return new Point(Math.round(x * temp) + trans_x,
				Math.round((y * sin_elevation + z * sz_cos) * -temp) + trans_y);
	}
}
