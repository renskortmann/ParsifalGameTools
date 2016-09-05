package parsifalgame.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import parsifalgame.ClientManager;
import parsifalgame.UIElements;
import parsifalgame.state.GameState;

public class ProjectorScreen extends TabbedScreen {

	public ProjectorScreen(UIElements uiElements, ClientManager manager) {
		super(uiElements, manager);
	}

	public String[] teamNames = {"Blue","Yellow","Green","Orange","Red","Black","White","Yellow-Black"};
	
	private TextButton shootOutTab = new TextButton("Shoot out", skin, "large");
	private TextButton towerHeightsTab = new TextButton("Torenhoogtes", skin, "large");
	private TextButton stockExchangeTab = new TextButton("Beurs", skin, "large");

	public final ShootOutSubScreen shootOutSubScreen = new ShootOutSubScreen(this, centralArea);
	public final TowerHeightsSubScreen towerHeightsSubScreen = new TowerHeightsSubScreen(this, centralArea);
	public final StockExchangeSubScreen stockExchangeSubScreen = new StockExchangeSubScreen(this, centralArea);

	@Override
	public void show() {
		super.show();

		tabBar.add(shootOutTab).expandX().fill();
		tabBar.add(towerHeightsTab).expandX().fill();
		tabBar.add(stockExchangeTab).expandX().fill();

		shootOutTab.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setToShootOut();
			}
		});

		towerHeightsTab.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setToTowerHeights();
			}
		});
		
		stockExchangeTab.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setToStockExchange();
			}
		});
		
		setToShootOut();
	}
	
	private void setToShootOut() {
		towerHeightsTab.setStyle(defaultStyle);
		stockExchangeTab.setStyle(defaultStyle);
		shootOutTab.setStyle(selectedStyle);
		shootOutSubScreen.show();
		visibleScreen = shootOutSubScreen;
	}

	private void setToTowerHeights() {
		shootOutTab.setStyle(defaultStyle);
		stockExchangeTab.setStyle(defaultStyle);
		towerHeightsTab.setStyle(selectedStyle);
		towerHeightsSubScreen.show();
		visibleScreen = towerHeightsSubScreen;
	}
	
	private void setToStockExchange() {
		shootOutTab.setStyle(defaultStyle);
		towerHeightsTab.setStyle(defaultStyle);
		stockExchangeTab.setStyle(selectedStyle);
		stockExchangeSubScreen.show();
		visibleScreen = stockExchangeSubScreen;
	}
	
	@Override
	public void updateGameState(int roleIndex, GameState newState) {
		super.updateGameState(roleIndex, newState);
		
		shootOutSubScreen.updateGameState(roleIndex, newState);
		towerHeightsSubScreen.updateGameState(roleIndex, newState);
		
		setToShootOut();
	}
}
