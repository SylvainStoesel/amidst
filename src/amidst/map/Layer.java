package amidst.map;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;

import MoF.ChunkManager;
import amidst.Log;

public class Layer implements Comparable<Layer> {
	public String name;
	public int size;
	public float depth;
	public float minZoom = 0;
	public float maxZoom = 1024;
	
	public double scale;
	private AffineTransform mat = new AffineTransform();
	
	private int[] defaultData;
	
	public boolean cacheEnabled;
	public CacheManager cacheManager;
	public String cachePath;
	
	private boolean live;
	protected ChunkManager chunkManager;
	
	public Layer(String name) {
		this(name, null);
	}
	public Layer(String name, CacheManager cacheManager) {
		this(name, cacheManager, 1f);
	}
	public Layer(String name, CacheManager cacheManager, float depth) {
		this(name, cacheManager, depth, Fragment.SIZE);
	}
	public Layer(String name, CacheManager cacheManager, float depth, int size) {
		this.name = name;
		this.cacheManager = cacheManager;
		this.cacheEnabled = (cacheManager != null);
		this.depth = depth;
		this.size = size;
		defaultData = new int[size*size];
		scale = ((double)Fragment.SIZE)/((double)size);
		for (int i = 0; i < defaultData.length; i++)
			defaultData[i] = 0x00000000;
	}
	
	public void unload(Fragment frag) {
		if (cacheEnabled) {
			cacheManager.unload(frag);
		}
	}
	
	public void setLive(boolean live) {
		this.live = live;
	}
	public Layer setMaxZoom(float maxZoom) {
		this.maxZoom = maxZoom;
		return this;
	}
	public Layer setMinZoom(float minZoom) {
		this.minZoom = minZoom;
		return this;
	}
	
	public int compareTo(Layer obj) {
		Layer lObj = (Layer)obj;
		if (depth < lObj.depth) return -1;
		return (depth > lObj.depth)?1:0;
	}
	
	public int[] getDefaultData() {
		return defaultData;
	}
	
	public void draw(Fragment frag, int layerID) {
		if (cacheEnabled) {
			cacheManager.load(frag, layerID);
		} else {
			drawCached(frag, layerID);
			//PluginManager.call(funcDraw, frag, layerID);
		}
	}
	
	public AffineTransform getMatrix(AffineTransform inMat) {
		mat.setTransform(inMat);
		return mat;
	}
	public AffineTransform getScaledMatrix(AffineTransform inMat) {
		mat.setTransform(inMat); mat.scale(scale, scale);
		return mat;
	}

	public boolean isLive() {
		return live;
	}
	
	public void drawCached(Fragment fragment, int layerID) {
		
	}
	
	public void drawLive(Fragment fragment, Graphics2D g, AffineTransform mat) {
		
	}
	public void setChunkManager(ChunkManager chunkManager) {
		this.chunkManager = chunkManager;
	}
}

