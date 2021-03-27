package erss.hwk3.ys319.qs33;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

public class XMLParserTest {
	@Test
	public void test_parse_create() throws ParserConfigurationException, SAXException, IOException {
		String toParse = 
			"<create>\n" +
			"   <account id=\"123456\" balance=\"1000\"/>\n" +
			"   <symbol sym=\"SYM\">\n" +
			"      <account id=\"123456\">20</account>\n" +
			"      <account id=\"123457\">30</account>\n" +
			"   </symbol>\n" +
			"</create>\n";
		RequestList requests = XMLParser.getRequestList(toParse);
		requests.collect();
		String actual = requests.toString();
		String expected = 
			"Creating an account with id 123456 and balance 1000.0\n" +
			"Creating a symbol with name SYM and:\n" +
			"  123456: 20\n" +
			"  123457: 30\n";
		assertEquals(expected, actual);
	}

	@Test
	public void test_parse_transact() throws ParserConfigurationException, SAXException, IOException {
		String toParse = 
			"<transactions id=\"123455\">\n" +
			"   <order sym=\"SYM\" amount=\"100\" limit=\"133.11\"/>\n" +
			"   <order sym=\"SYM\" amount=\"-100\" limit=\"133.11\"/>\n" +
			"   <query id=\"135678\"/>\n" +
			"   <cancel id=\"122333\"/>\n" +
			"</transactions>\n";
		RequestList requests = XMLParser.getRequestList(toParse);
		requests.collect();
		String actual = requests.toString();
		String expected = 
			"Opening a buying order by 123455 of 100 SYM with limit 133.11\n" +
			"Opening a selling order by 123455 of 100 SYM with limit 133.11\n" +
			"Querying 135678 of account 123455\n" +
			"Cancelling 122333 of account 123455\n";
		assertEquals(expected, actual);
	}

}
