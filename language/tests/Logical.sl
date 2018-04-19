def main() {
  t = 10 == 10; // true
  f = 10 != 10; // false
  println(left(f) && right(f));
  println(left(f) && right(t));
  println(left(t) && right(f));
  println(left(t) && right(t));
  println("");
  println(left(f) || right(f));
  println(left(f) || right(t));
  println(left(t) || right(f));
  println(left(t) || right(t));
}

def left(x) {
  println("left");
  return x;
}

def right(x) {
  println("right");
  return x;
}
