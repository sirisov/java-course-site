package ale.java_training.classes;

import static org.testng.Assert.assertEquals;

import java.util.Random;

import org.testng.annotations.Test;

public class FighterTest {

  @Test
  public void basicTests() {
    assertEquals(FightClub.declareWinner(new Fighter("Lew", 10, 2), new Fighter("Harry", 5, 4), "Lew"), "Lew");
    assertEquals(FightClub.declareWinner(new Fighter("Lew", 10, 2), new Fighter("Harry", 5, 4), "Harry"), "Harry");
    assertEquals(FightClub.declareWinner(new Fighter("Harald", 20, 5), new Fighter("Harry", 5, 4), "Harry"), "Harald");
    assertEquals(FightClub.declareWinner(new Fighter("Harald", 20, 5), new Fighter("Harry", 5, 4), "Harald"), "Harald");
    assertEquals(FightClub.declareWinner(new Fighter("Jerry", 30, 3), new Fighter("Harald", 20, 5), "Jerry"), "Harald");            
    assertEquals(FightClub.declareWinner(new Fighter("Jerry", 30, 3), new Fighter("Harald", 20, 5), "Harald"), "Harald");
  }
  
  @Test
  public void randomTests() {
    String[] names = {"Willy", "Johnny", "Max", "Lui", "Marco", "Bostin", "Loyd", "Mark", "Cuban", "Lew", "Rocky", "Mario", "David", "Patrick", "Michael"};
    Random r = new Random();
    for (int i = 0; i < 200; i++) {
      String[] name = {names[r.nextInt(names.length)], names[r.nextInt(names.length)]};
      while (name[0].equals(name[1])) {
        name[1] = names[r.nextInt(names.length)];
      }
      int[] health = {r.nextInt(999) + 1, r.nextInt(999) + 1}, damagePerAttack = {r.nextInt(99) + 1, r.nextInt(99) + 1};
      String first = name[r.nextInt(1)];
      assertEquals(FightClub.declareWinner(new Fighter(name[0], health[0], damagePerAttack[0]), new Fighter(name[1], health[1], damagePerAttack[1]), first), solution(new Fighter(name[0], health[0], damagePerAttack[0]), new Fighter(name[1], health[1], damagePerAttack[1]), first));
    }
  }
  
  private static String solution(Fighter f1, Fighter f2, String firstAttacker) {
		switch (Double.compare(Math.ceil(f2.getHealth()*1.0/f1.getDPA()), Math.ceil(f1.getHealth()*1.0/f2.getDPA()))) {
      case -1: return f1.getName();
      case 0: return firstAttacker;
      case 1: return f2.getName();
    }
    return null;
	}

}
