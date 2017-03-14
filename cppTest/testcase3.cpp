#include <iostream>
using namespace::std;

int main(int argc, char *argv[]) {
	int a;
	cin<<a;
	int b = 1;
	while((b <= a)){
		if((a % b) == 0){
			cout<<b<<endl;
		}
		b = (b + 1);
	}
	cin<<a;
	return 0;
}
