package com.personal.hwplayer.ui;

import com.personal.hwplayer.utils.ActivityQueue;

/**
 * The subclass of this interface is stackable that can push into or pop from itself activity stack.
 * for example, some activity may be pushed into a {@link ActivityQueue} so those activities
 * should implement this interface.
 * 
 * @author liukai
 * @since 2014-11-7
 */
public interface Stackable {
	/**
	 * Push self into an stack
	 */
	public abstract void push();
	
	/**
	 * Pop self from an stack
	 */
	public abstract void pop();
	
}
