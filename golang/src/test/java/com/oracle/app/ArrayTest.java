package com.oracle.app;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.vm.PolyglotEngine;

public class ArrayTest {
	private PolyglotEngine engine;
	private PolyglotEngine.Value math;

	@Before
	public void setUp() throws Exception {
		engine = PolyglotEngine.newBuilder().build();
		Source source = Source.newBuilder(""
				+"package main\n"
				+"\n"
				+"func main() int{ \n"
				+"    var a [5] int \n"
				+ "	  result := 0 \n"
				+"	  for i := 0; i < len(a); i++{ \n"
				+"	  	result += a[i] \n"
				+"    }\n"
				+"    b := [5]int{1 , 2, 3, 4, 5} \n"
				+ "	  b[4] = 10 \n"
				+ "	  for j := 0; j < len(b); j++ { \n"
				+ "		result += b[j] \n"
				+ "	  } \n"
				+ "	  return result \n"
				+"}").
		name("UnitTest.go").
		mimeType("text/x-go").
		build();
		ToolChain.executeCommands(source);
		engine.eval(source);
		math = engine.findGlobalSymbol("main");
	}

	@After
	public void tearDown() throws Exception {
		engine.dispose();
	}

	@Test
	public void test() throws Exception{
		
		Number ret = math.execute().as(Number.class);
		assertEquals(20,ret.intValue());
		
	}
}
