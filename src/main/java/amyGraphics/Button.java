package amyGraphics;

public class Button extends Container {
	
	private boolean pressed;
	
	private Texture releaseTexture;
	private Texture pressTexture;
	
	public Button() {
		super();
	}
	
	public Button(Texture releaseTexture, Texture pressTexture) {
		this();
		
		setButtonTextures(releaseTexture, pressTexture);
	}
	
	public void setReleaseTexture(Texture releaseTexture) {
		removeTexture(this.releaseTexture);
		addTexture(releaseTexture);
		
		this.releaseTexture = releaseTexture;
		
		updateButtonTexture();
	}
	
	public void setPressTexture(Texture pressTexture) {
		removeTexture(this.pressTexture);
		addTexture(pressTexture);
		
		this.pressTexture = pressTexture;
		
		updateButtonTexture();
	}
	
	public void setButtonTextures(Texture releaseTexture, Texture pressTexture) {
		setReleaseTexture(releaseTexture);
		setPressTexture(pressTexture);
	}
	
	public void updateButtonTexture() {
		if (pressed) {
			setActiveTexture(pressTexture);
		} else {
			setActiveTexture(releaseTexture);
		}
	}
	
	public void setPressed(boolean pressed) {
		this.pressed = pressed;
		
		updateButtonTexture();
	}
	
	public Texture getReleaseTexture() {
		return releaseTexture;
	}
	
	public Texture getPressTexture() {
		return pressTexture;
	}
}
