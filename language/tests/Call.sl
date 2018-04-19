def ret(a) { return a; } 
def dub(a) { return a * 2; } 
def inc(a) { return a + 1; } 
def dec(a) { return a - 1; } 
def call(f, v) { return f(v); }
 
def main() {  
  println(ret(42));
  println(dub(21));
  println(inc(41));
  println(dec(43));
  println(call(ret, 42));
  println(call(dub, 21));
  println(call(inc, 41));
  println(call(dec, 43));
}  
