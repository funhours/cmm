package com.mlongbo.jfinal.render;

import com.jfinal.render.Render;
import com.jfinal.render.RenderFactory;

public class MyRenderFactory extends RenderFactory {
	public Render getJsonRender(){
		return new JsonRender();
	}
	
}
