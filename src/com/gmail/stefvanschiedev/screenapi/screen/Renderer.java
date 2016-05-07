package com.gmail.stefvanschiedev.screenapi.screen;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapFont;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class Renderer extends MapRenderer {
	
	private List<Pixel> pixelsToDraw = new ArrayList<>();
	private List<Text> textsToDraw = new ArrayList<>();
	
	@Override
	public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
		for (Pixel pixel : pixelsToDraw) {
			mapCanvas.setPixel(pixel.getX(), pixel.getY(), pixel.getColor());
		}
		
		pixelsToDraw.clear();
		
		for (Text text : textsToDraw) {
			mapCanvas.drawText(text.getX(), text.getY(), text.getFont(), text.getText());
		}
		
		textsToDraw.clear();
	}
	
	public void setPixel(int x, int y, byte color) {
		pixelsToDraw.add(new Pixel(x, y, color));
	}
	
	public void drawText(int x, int y, MapFont font, String text) {
		textsToDraw.add(new Text(x, y, font, text));
	}
}