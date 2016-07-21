package parsifalgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


public class UIElements {

	public static final boolean DEBUG = false;

	private Skin skin;
	private TextureAtlas atlas;

	private Texture loadingIcon;
	
	private static final FreeTypeFontParameter small = new FreeTypeFontParameter();
	private static final FreeTypeFontParameter medium = new FreeTypeFontParameter();
	private static final FreeTypeFontParameter large = new FreeTypeFontParameter();

	private static final String style_normal = "DroidSerif.ttf";
	private static final String style_bold = "DroidSerif-Bold.ttf";
	private static final String style_bolditalic = "DroidSerif-BoldItalic.ttf";
	private static final String style_italic = "DroidSerif-Italic.ttf";

	static {
		small.size = 12;
		medium.size = 17;
		large.size = 27;
	}

	void create() {
		if (skin != null)
			skin.dispose();

		skin = new Skin();
		atlas = new TextureAtlas(Gdx.files
				.internal("uiskin.atlas"));
		for (Texture t : atlas.getTextures())
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		skin.addRegions(atlas);

		skin.add("small-font", getFont(style_normal, small));
		skin.add("medium-font", getFont(style_normal, medium));
		skin.add("large-font", getFont(style_normal, large));

		skin.add("small-bold-font", getFont(style_bold, small));
		skin.add("medium-bold-font", getFont(style_bold, medium));
		skin.add("large-bold-font", getFont(style_bold, large));

		skin.add("small-bold-italic-font", getFont(style_bolditalic, small));
		skin.add("medium-bold-italic-font", getFont(style_bolditalic, medium));
		skin.add("large-bold-italic-font", getFont(style_bolditalic, large));

		skin.add("small-italic-font", getFont(style_italic, small));
		skin.add("medium-italic-font", getFont(style_italic, medium));
		skin.add("large-italic-font", getFont(style_italic, large));

		skin.load(Gdx.files.internal("uiskin.json"));
		
		loadingIcon = new Texture(Gdx.files.internal("loadingicon.png"));
		loadingIcon.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

	void dispose() {
		if (skin != null) {
			skin.dispose();
			atlas.dispose();
			skin = null;
		}
	}
	
	public Texture getLoadingIcon() {
		return loadingIcon;
	}
	
	public Skin getSkin() {
		return skin;
	}

	private BitmapFont getFont(String internalPath, FreeTypeFontParameter param) {
		FileHandle file = Gdx.files.internal(internalPath);
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(file);
		BitmapFont bfont = gen.generateFont(param);
		gen.dispose();
		return bfont;
	}

}
