package movement;

import java.awt.Color;

public abstract class Light extends Entity {
	private LightType type;
	private Color lightColor;
	private double ambient;
	private double diffuse;
	private double specular;

	public Light(LightType type) {
		if (type == null) {
			throw new NullPointerException("Type cannot be null");
		}
		setType(type);
		setColor(Color.WHITE);
	}

	public Light(LightType type, Color color) {
		this(type);
		setColor(color);
	}

	public void setColor(Color color) {
		this.lightColor = color;
	}

	public Color getColor() {
		return lightColor;
	}

	public void setType(LightType type) {
		this.type = type;
	}

	public LightType getType() {
		return type;
	}

	public double getAmbient() {
		return ambient;
	}

	public void setAmbient(double ambient) {
		this.ambient = ambient;
	}

	public double getDiffuse() {
		return diffuse;
	}

	public void setDiffuse(double diffuse) {
		this.diffuse = diffuse;
	}

	public double getSpecular() {
		return specular;
	}

	public void setSpecular(double specular) {
		this.specular = specular;
	}

}
