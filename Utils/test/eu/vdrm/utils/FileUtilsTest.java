package eu.vdrm.utils;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.Assert;
public class FileUtilsTest {
	
	@Test
	public void test1() {
		//Path path = Paths.get("D:", "_ulrich", "bomen");
		Path path = Paths.get("D:", "workspaces", "libraries");
		Path result  = FileUtils.getNewestFile(path);
		Assert.assertEquals("D:\\workspaces\\libraries\\junit4\\hamcrest-core-1.3-javadoc.jar", result.toString());
	}

}
