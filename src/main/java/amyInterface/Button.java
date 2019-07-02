package amyInterface;

import amyGraphics.Texture;

public class Button extends Container implements Hoverable {

	private boolean pressed;
	private boolean hover;

	private Texture releaseTexture;
	private Texture pressTexture;
	private Texture hoverTexture;

	public Button() {
		super();
		
		setInteractable(true);
	}

	public Button(Texture releaseTexture, Texture pressTexture, Texture hoverTexture) {
		this();

		setButtonTextures(releaseTexture, pressTexture, hoverTexture);
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
	
	public void setHoverTexture(Texture hoverTexture) {
		removeTexture(this.hoverTexture);
		addTexture(hoverTexture);
		
		this.hoverTexture = hoverTexture;
		
		updateButtonTexture();
	}

	public void setButtonTextures(Texture releaseTexture, Texture pressTexture, Texture hoverTexture) {
		setReleaseTexture(releaseTexture);
		setPressTexture(pressTexture);
		setHoverTexture(hoverTexture);
	}

	public void updateButtonTexture() {
		if (pressed) {
			setActiveTexture(pressTexture);
		} else if (hover) {
			if (hoverTexture != null) setActiveTexture(hoverTexture);
		} else {
			setActiveTexture(releaseTexture);
		}
	}

	public void setPressed(boolean pressed) {
		this.pressed = pressed;

		updateButtonTexture();
	}
	
	public void setHover(boolean hover) {
		this.hover = hover;
		
		updateButtonTexture();
	}

	public Texture getReleaseTexture() {
		return releaseTexture;
	}

	public Texture getPressTexture() {
		return pressTexture;
	}

	@Override
	public void mouseOn() {
		setHover(true);
	}

	@Override
	public void mouseOff() {
		setHover(false);
	}
}
