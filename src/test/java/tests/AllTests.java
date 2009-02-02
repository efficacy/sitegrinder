package tests;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
    
    public static TestSuite suite() {
        TestSuite ret = new TestSuite();

        ret.addTestSuite(GrinderLoadSaveTest.class);
        ret.addTestSuite(MainTest.class);
        ret.addTestSuite(GrindTraversalTest.class);
        ret.addTestSuite(TemplateTransformerTest.class);

        return ret;
    }
}
