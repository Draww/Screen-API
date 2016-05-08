package com.gmail.stefvanschiedev.screenapi.screen;

import java.awt.Image;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapFont;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

public class Screen {

	//screen size = 12:7 ; 1536:896
	
	private ItemFrame[][] maps;
	private MapView[][] views;
	private Player player;
	private byte color;
	private MapFont font;
	
	public Screen(Player player) {
		this.maps = new ItemFrame[12][7];
		this.views = new MapView[12][7];
		this.player = player;
		this.color = Color.TRANSPARENT;
		this.font = MinecraftFont.Font;
		
		//create mapviews
		for (int x = 0; x < 12; x++) {
			for (int y = 0; y < 7; y++)
				views[x][y] = Bukkit.createMap(player.getWorld());
		}
		
		//clear all maps
		for (int x = 0; x < 12; x++) {
			for (int y = 0; y < 7; y++) {
				views[x][y].getRenderers().clear();
				views[x][y].addRenderer(new Renderer());
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void show() {
		player.teleport(new Location(player.getWorld(), player.getLocation().getBlockX() + 1, player.getLocation().getBlockY(), player.getLocation().getBlockZ() + .13, 0, 0));
		
		for (int x = 0; x < 12; x++) {
			for (int y = 0; y < 7; y++) {
				player.getWorld().getBlockAt(player.getLocation().add(5 - x, 4 - y, 4)).setType(Material.AIR);
				player.getWorld().getBlockAt(player.getLocation().add(5 - x, 4 - y, 5)).setType(Material.BARRIER);
				maps[x][y] = ((ItemFrame) player.getWorld().spawnEntity(player.getLocation().clone().add(5 - x, 4 - y, 4), EntityType.ITEM_FRAME));
				maps[x][y].setFacingDirection(BlockFace.NORTH);
			}
		}
		
		for (int x = 0; x < 12; x++) {
			for (int y = 0; y < 7; y++)
				maps[x][y].setItem(new ItemStack(Material.MAP, 1, views[x][y].getId()));
		}
	}
	
	public void setColor(byte color) {
		this.color = color;
	}
	
	public void setFont(MapFont font) {
		this.font = font;
	}
	
	public byte getColor() {
		return color;
	}
	
	public MapFont getFont() {
		return font;
	}
	
	public void drawPixel(int x, int y) {
		if (x < 0 || x > 1536 || y < 0 || y > 896)
			return;
		
		((Renderer) views[(int) (x / 128)][(int) (y / 128)].getRenderers().get(1)).setPixel(x - (((int) (x / 128)) * 128), y - (((int) (y / 128)) * 128), color);
	}
	
	public void drawText(int x, int y, String text) {
		if (x < 0 || x > 1536 || y < 0 || y > 896)
			return;

		((Renderer) views[(int) (x / 128)][(int) (y / 128)].getRenderers().get(1)).drawText(x - (((int) (x / 128)) * 128), y - (((int) (y / 128)) * 128), font, text);
	}
	
	@SuppressWarnings("deprecation")
	public void drawImage(int startX, int startY, Image image) {
		if (startX < 0 || startX > 1536 || startY < 0 || startY > 896)
			return;
		
		byte bytes[] = MapPalette.imageToBytes(image);
		for (int x = 0; x < image.getWidth(null); x++) {
			for (int y = 0; y < image.getHeight(null); y++) {
				setColor(bytes[y * image.getWidth(null) + x]);
				drawPixel(startX + x, startY + y);
			}
		}
	}
	
	public void drawRect(int startX, int startY, int width, int height) {
		drawLine(startX, startY, startX + width, startY);
		drawLine(startX, startY, startX, startY + height);
		drawLine(startX, startY + height, startX + width, startY + height);
		drawLine(startX + width, startY, startX + width, startY + height);
	}
	
	/*
	 * From: https://en.wikipedia.org/wiki/Bresenham's_line_algorithm#Method
	 */
	public void drawLine(int startX, int startY, int endX, int endY) {
		if (startX > endX && startY > endY) {
			int startXCopy = startX;
			startX = endX;
			endX = startXCopy;
			
			int startYCopy = startY;
			startY = endY;
			endY = startYCopy;
		}
		
		double deltaX = endX - startX;
		double deltaY = endY - startY;
		double error = -1;
		double deltaError = Math.abs(deltaY / deltaX);

		if (deltaX == 0) {
			for (int y = startY; y < endY; y++)
				drawPixel(startX, y);
		}
		
		if (deltaY == 0) {
			for (int x = startX; x < endX; x++)
				drawPixel(x, startY);
		}
		
		if (deltaX >= deltaY) {
			int y = startY;
			int yDirection = endY > startY ? 1 : -1;
			
			for (int x = startX; x < endX - 1; x++) {
				error += deltaError;
			
				if (error >= 0) {
					y += yDirection;
					error--;
				}
				
				drawPixel(x, y);
			}
		} else if (deltaY > deltaX) {
			int x = startX;
			int xDirection = endX > startX ? 1 : -1;
			
			for (int y = startY; y < endY - 1; y++) {
				error += deltaError;
			
				if (error >= 0) {
					x += xDirection;
					error--;
				}
				
				drawPixel(x, y);
			}
		}
	}
	
	@Deprecated
	public void drawPolygon(int[] xs, int[] ys) {
		if (xs.length != ys.length)
			return;
		
		for (int i = 1; i < xs.length; i++) {
			System.out.println("Drawing line " + i + " with locations " + xs[i - 1] + " " + ys[i - 1] + " " + xs[i] + " " + ys[i]);
			drawLine(xs[i - 1], ys[i - 1], xs[i], ys[i]);
		}
		
		//needs work
		System.out.println("Drawing line from " + xs[0] + " " + ys[0] + " to " + xs[xs.length - 1] + " " + ys[ys.length - 1]);
		drawLine(xs[0], ys[0], xs[xs.length - 1], ys[ys.length - 1]);
	}
	
	public void fillRect(int startX, int startY, int width, int height) {
		for (int x = startX; x < startX + width; x++) {
			for (int y = startY; y < startY + height; y++)
				drawPixel(x, y);
		}
	}
}