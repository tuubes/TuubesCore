import com.electronwill.utils.IndexMap;
import java.util.Iterator;
import org.junit.jupiter.api.Test;

/**
 * @author TheElectronWill
 */
public class IndexMapTest {
	@Test
	public void testIterator() {
		IndexMap<String> indexMap = new IndexMap<>();
		indexMap.put(0, "0");
		indexMap.put(5, "5");
		indexMap.put(10, "10");
		Iterator<String> it = indexMap.values().iterator();
		System.out.println(it.next());
		System.out.println(it.next());
		System.out.println(it.next());
	}
}
