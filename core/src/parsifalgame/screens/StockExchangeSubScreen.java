package parsifalgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class StockExchangeSubScreen extends SubScreen {

	private static final int NUM_LOGOS = 3;
	private static final int NUM_LOGO_DISPLAYS = 30;
	
	private TextureRegionDrawable[] logos;
	private Image logo = new Image();

	private java.util.Random rng = new java.util.Random(System.currentTimeMillis());
	
	private class RollDice extends Thread {
		public void run() {
			int currentLogo = rng.nextInt(NUM_LOGOS);
			int nextLogo;
			long showDuration = 50;

			for(int i = 0; i < NUM_LOGO_DISPLAYS; i++) {
				
				logo.setDrawable(logos[currentLogo]);

				try {
					Thread.sleep(showDuration);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				do {
					nextLogo = rng.nextInt(NUM_LOGOS);
				} while(nextLogo == currentLogo);
				currentLogo = nextLogo;
				
				showDuration *= 1.1;			
			}			
		}
	}

	public StockExchangeSubScreen(BasicScreen parentScreen, Table parentTable) {
		super(parentScreen, parentTable);

		logos = new TextureRegionDrawable[3];

		logos[0] = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("HighTechAsia.png"))));
		logos[1] = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("CountryInvestmentTrust.png"))));
		logos[2] = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("EcoScience.png"))));
}
	
	@Override
	public void show() {
		super.show();

			mainTable.center();
			mainTable.setSkin(skin);
			
			mainTable.add(logo).prefSize(600);
			mainTable.row();

			TextButton btn_payOut = new TextButton("Bespeel de beurs", skin, "large");		
			mainTable.add(btn_payOut).colspan(NUM_LOGOS).padTop(100);
			mainTable.row();

			btn_payOut.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y) {
					new Thread(new RollDice()).start();
				}
			});
			mainTable.row();
	}
	
	@Override
	protected void update() {
	}

}
