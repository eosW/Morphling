#include <iostream>
using namespace::std;

int main(int argc, char *argv[]) {
	int a;
	cin<<a;
	if(a < 0){
		a = (-a);
	}
	else if(a > 100){
		while((a > 5)){
			a = (a / 2);
		}
	}
	else {
		while(true){
			a = (a - 1);
			if(((a % 3) == 0) && (a < 0)){
				break;
			}
		}
	}
	cout<<a<<endl;
	return 0;
}
