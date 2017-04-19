package ale.java_training;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

@Test(timeOut = 5_000)
public class FindAccountTest {

  public void test() {
    assertEquals(AccountSearcher.findAccountByPiece(new String[]{"13049523", "4912095", "94919571", "64039217", "31920168"}, "40392"), "64039217");
    assertEquals(AccountSearcher.findAccountByPiece(new String[]{"abcd", "asdfg", "qwet", "bbb", "werwer"}, "a"), "abcd");
    assertEquals(AccountSearcher.findAccountByPiece(new String[]{"abcd", "asdfg", "qwet", "bbb", "werwer"}, "bb"), "bbb");
    assertEquals(AccountSearcher.findAccountByPiece(new String[]{"abcd", "asdfg", "qwet", "bbb", "werwer"}, "wer"), "werwer");
  }
  
}
