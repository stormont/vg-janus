package com.voyagegames.janus;


public interface ITransformer <T, U> {
	
	U transform(T input);

}
