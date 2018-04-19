def add(a, b) {
  return a + b;
}

def loop(n) {
  i = 0;
  while (i < n) {  
    i = add(i, 1);  
  }
  return i;
}

def main() {
  i = 0;
  while (i < 20) {
    loop(1000);
    i = i + 1;
  }
  println(loop(1000));  
}
