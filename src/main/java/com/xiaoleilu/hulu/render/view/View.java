package com.xiaoleilu.hulu.render.view;

/**
 * 视图接口，此接口用于在Action方法中做为返回对象而使用的接口<br>
 * 请自定义实现此接口或使用框架定义的几个实现类来达到返回不同类型内容的目的<br>
 * 框架会在Action方法调用结束后调用Render方法以实现向客户端写入数据，如果你自己实现这个接口，请在render方法中实现写入Response。
 * @author Looly
 *
 */
public interface View {
	/**
	 * 写入数据到客户端
	 */
	public void render();
}
