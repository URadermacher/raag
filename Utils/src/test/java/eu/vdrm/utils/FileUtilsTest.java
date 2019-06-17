package eu.vdrm.utils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;



public class FileUtilsTest {

	@Disabled
	@Test
	public void test1() {
		Path path = Paths.get("D:", "_ulrich", "bomen");
		//Path path = Paths.get("D:", "workspaces", "libraries");
		Path result  = FileUtils.getNewestFile(path);
		System.out.println(result.toString());
		//Assert.assertEquals("D:\\workspaces\\libraries\\junit4\\hamcrest-core-1.3-javadoc.jar", result.toString());
	}

}
