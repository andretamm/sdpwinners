package sdp.vision;

import java.awt.Color;
import java.io.Serializable;

public class ObjectThresholdState implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int r_low;
	private int r_high;
	private int g_low;
	private int g_high;
	private int b_low;
	private int b_high;
	private double h_low;
	private double h_high;
	private double s_low;
	private double s_high;
	private double v_low;
	private double v_high;
	private int rg_low;
	private int rg_high;
	private int rb_low;
	private int rb_high;
	private int gb_low;
	private int gb_high;

	@Override
	public String toString() {
		return "ObjectThresholdState [r_low=" + r_low + ", r_high=" + r_high
				+ ", g_low=" + g_low + ", g_high=" + g_high + ", b_low="
				+ b_low + ", b_high=" + b_high + ", h_low=" + h_low
				+ ", h_high=" + h_high + ", s_low=" + s_low + ", s_high="
				+ s_high + ", v_low=" + v_low + ", v_high=" + v_high
				+ ", rg_low=" + rg_low + ", rg_high=" + rg_high + ", rb_low="
				+ rb_low + ", rb_high=" + rb_high + ", gb_low=" + gb_low
				+ ", gb_high=" + gb_high + "]";
	}

	public ObjectThresholdState() {
		this.r_low = 0;
		this.r_high = 255;
		this.g_low = 0;
		this.g_high = 255;
		this.b_low = 0;
		this.b_high = 255;
		this.h_low = 0;
		this.h_high = 255;
		this.s_low = 0;
		this.s_high = 255;
		this.v_low = 0;
		this.v_high = 255;
		this.rg_low = -255;
		this.rg_high = 255;
		this.rb_low = -255;
		this.rb_high = 255;
		this.gb_low = -255;
		this.gb_high = 255;
	}
	
	public int get_r_low() {
		return r_low;
	}

	public void set_r_low(int r_low) {
		this.r_low = r_low;
	}
	
	public int get_r_high() {
		return r_high;
	}

	public void set_r_high(int r_high) {
		this.r_high = r_high;
	}
	
	
	public int get_g_low() {
		return g_low;
	}

	public void set_g_low(int g_low) {
		this.g_low = g_low;
	}
	
	public int get_g_high() {
		return g_high;
	}

	public void set_g_high(int g_high) {
		this.g_high = g_high;
	}
	
	
	public int get_b_low() {
		return b_low;
	}

	public void set_b_low(int b_low) {
		this.b_low = b_low;
	}
	
	public int get_b_high() {
		return b_high;
	}

	public void set_b_high(int b_high) {
		this.b_high = b_high;
	}
	
	public double get_h_low() {
		return h_low;
	}

	public void set_h_low(double h_low) {
		this.h_low = h_low;
	}
	
	public double get_h_high() {
		return h_high;
	}

	public void set_h_high(double h_high) {
		this.h_high = h_high;
	}
	
	
	public double get_s_low() {
		return s_low;
	}

	public void set_s_low(double s_low) {
		this.s_low = s_low;
	}
	
	public double get_s_high() {
		return s_high;
	}

	public void set_s_high(double s_high) {
		this.s_high = s_high;
	}
	
	
	public double get_v_low() {
		return v_low;
	}

	public void set_v_low(double v_low) {
		this.v_low = v_low;
	}
	
	public double get_v_high() {
		return v_high;
	}

	public void set_v_high(double v_high) {
		this.v_high = v_high;
	}
	
	
	public int get_rg_low() {
		return rg_low;
	}

	public void set_rg_low(int rg_low) {
		this.rg_low = rg_low;
	}
	
	public int get_rg_high() {
		return rg_high;
	}

	public void set_rg_high(int rg_high) {
		this.rg_high = rg_high;
	}
	
	
	public int get_rb_low() {
		return rb_low;
	}

	public void set_rb_low(int rb_low) {
		this.rb_low = rb_low;
	}
	
	public int get_rb_high() {
		return rb_high;
	}

	public void set_rb_high(int rb_high) {
		this.rb_high = rb_high;
	}
	
	
	public int get_gb_low() {
		return gb_low;
	}

	public void set_gb_low(int gb_low) {
		this.gb_low = gb_low;
	}
	
	public int get_gb_high() {
		return gb_high;
	}

	public void set_gb_high(int gb_high) {
		this.gb_high = gb_high;
	}


	/**
	 * Determines if a pixel is part of the object, based on input RGB colours and
	 * hsv values.
	 * 
	 * @param color
	 *            The RGB colours for the pixel.
	 * @param hsbvals
	 *            The HSV values for the pixel.
	 * 
	 * @return True if the RGB and HSV values are within the defined thresholds
	 *         (and thus the pixel is part of the ball), false otherwise.
	 */
	public boolean isColour(Color colour, float[] hsbvals, int rg, int rb, int gb) {
		return (hsbvals[0] <= get_h_high()
				&& hsbvals[0] >= get_h_low()
				&& hsbvals[1] <= get_s_high()
				&& hsbvals[1] >= get_s_low()
				&& hsbvals[2] <= get_v_high()
				&& hsbvals[2] >= get_v_low()
				&& colour.getRed() <= get_r_high()
				&& colour.getRed() >= get_r_low()
				&& colour.getGreen() <= get_g_high()
				&& colour.getGreen() >= get_g_low()
				&& colour.getBlue() <= get_b_high()
				&& colour.getBlue() >= get_b_low())
				&& rg <= get_rg_high()
				&& rg >= get_rg_low()
				&& rb <= get_rb_high()
				&& rb >= get_rb_low()
				&& gb <= get_gb_high()
				&& gb >= get_gb_low();
	}

}
