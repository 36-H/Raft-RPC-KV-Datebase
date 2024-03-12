package cn.hiles.test.scanner;

import cn.hiles.common.scanner.ClassScanner;
import cn.hiles.common.scanner.server.RpcServiceScanner;
import org.junit.Test;

import java.util.List;

/**
 * @author Helios
 * Time：2024-03-11 9:44
 */
public class ScannerTest {
    @Test
    public void testScannerClassNameList() throws Exception {
        List<String> classNames = ClassScanner
                .getClassNameList("cn.hiles.test.scanner");
        classNames.forEach(System.out::println);
    }

    @Test
    public void testScannerClassNameListByRpcService() throws Exception {
        RpcServiceScanner
                .doScanWithRpcServiceAnnotationFilterAndRegistryService("cn.hiles.test.scanner");
    }
}
