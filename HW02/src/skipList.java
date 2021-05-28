/* Andreas Hinsch
 *
 *  an478360
 *
 *  COP3505
 *  
 *  3/15/2020
 */
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

public class skipList {
public static void main(String[]args)throws IOException{
	

	
try {
FileWriter output = new FileWriter("STDOUT.txt");
FileReader input = new FileReader("input.txt");
Scanner reader = new Scanner(input);

String instruction;

int seed = 42;
if(args.length == 2) {
	seed = Integer.parseInt(args[1]);
}	

output.write("For the input File named "+input+"\n");
if(args.length == 2) {
	output.write("With the RNG seeded,\n");
}
else {
	output.write("With the RNG unseeded,\n");
}
	
//Creates the two infinite end nodes
Node root = new Node();
root.Infinite = "---infinity";
root.up = null;
root.down = null;
Node posInfinity = new Node();
posInfinity.Infinite = "+++infinity";
root.next = posInfinity;
posInfinity.prev = root;
posInfinity.up = null;
posInfinity.down = null;
posInfinity.val = 5001;
root.val = -1;

Node Search;


Random Rand = new Random(seed);
int randNum = 0;

int printsNum;
int printsNum2;
//reads the file until end
while(reader.hasNextLine()) {

instruction = reader.next();


switch(instruction) {
case "i": Insert(reader.nextInt(), root, posInfinity, Rand, randNum); break;
case "s": printsNum = reader.nextInt();
Search = Search(printsNum, root);
if(Search != root) {output.write(printsNum+" integer found ");}
else {
output.write(printsNum+"integer not found");
}
break;
case "d": printsNum = reader.nextInt();
printsNum2 = Delete(printsNum , root);
if(printsNum2 == 1) {output.write(printsNum+" deleted\n");}
else {
	output.write(printsNum+"integer not found - delete not successful\n");
}
break;
case "p": printAll(root, output, args);break;
case "q":
	input.close();
	reader.close();
	output.close();
	System.out.println("process complete");
	System.exit(0);
}
}


}
//ends program if file is not found
catch(FileNotFoundException e) {
System.out.println("file not found");
System.exit(1);
}


}

//Inserts a new node
public static Node Insert(int num, Node root, Node end, Random Rand, int randNum){

//checks to see if the node is within the range 0-5000 and that its not already in the list
if(num > 0) {
if(num <= 5000) {
if(Search(num, root) == root) {

//Chunk1: creates the new Node fills in its values and places it correctly into the list
Node current = root;
Node Insert = new Node();
Node previous;
Insert.val = num;
Insert.up = null;
Insert.down = null;
Insert.Infinite = "no";

//Chunk2: places the pointer as close to the point of insertion as possible then traverses the bottom linked list until the location is found
while(current.up != null) {
current = current.up;
}
while(current.down != null) {
while(current.Infinite != "+++infinity" && current.val < Insert.val) {
current = current.next;
}
current = current.prev.down;
}
while(current.Infinite != "+++infinity" && current.val < Insert.val) {
current = current.next;
}
//End of Chunk2

//Links the new node into the list
    previous = current.prev;
Insert.prev = previous;
Insert.next = current;
current.prev = Insert;
previous.next = Insert;
Insert.next = current;
//end of Chunk1

//Creates a random number

//Handels adding in any up nodes
Promote(root, Insert, end, Rand, randNum);
}
}
}
else {
System.out.println("num too big or smol");

}
return root;

}

//Handels linking any upper nodes
public static Node Promote(Node root, Node Insert, Node end, Random Rand, int randNum) {
//Random Rand2 = new Random(42);
	randNum = Rand.nextInt() % 2;

//Flips a fair coin
if(randNum == 1) {
    Node current;
Node vertical = root;

//Chunk1: If we need to create another upper level create one
if(root.up == null) {
//Creates the neginfinity base node
Node temp = new Node();
temp.val = root.val;
temp.Infinite = root.Infinite;
temp.up = null;
temp.down = root;
root.up = temp;

//Creates the posinfinity base node
Node newNode = new Node();
newNode.val = end.val;
newNode.Infinite = end.Infinite;
newNode.up = null;
newNode.down = end;
end.up = newNode;

//Links the two new nodes
temp.next = end;
newNode.prev = temp;

}
//End of Chunk1

//Creates the Node to be inserted
current = new Node();
current.val = Insert.val;
current.Infinite = "no";
current.up = null;
current.down = Insert;
Insert.up = current;

//Chunk2: Finds where in the upper list to put the node
Node upperLocator = root.up;
//If this is not the first entry into this level
if(upperLocator.next.val != 5001) {
//find where in the upper level we should place the node
while(upperLocator.val < current.val) {
upperLocator = upperLocator.next;
}

//Links the new node to the upper level of the list
   Node prev = upperLocator.prev;
current.prev = prev;
current.next = upperLocator;
upperLocator.prev = current;
prev.next = current;
current.next = upperLocator;


}
//If this is the first entry we can link this new upper node with the two upper Infinite nodes
else {
vertical.up.next = current;
end.up.prev = current;
current.next = end.up;
current.prev = vertical.up;
}

//End of chunk2

//Recursive call in case we need to add more levels
Promote(vertical.up, Insert.up, end.up, Rand, randNum);

}

return Insert;

}

//Searches for if the node is in the SkipList
public static Node Search(int num,Node root){
if(num > 0 && num <= 5000) {
//traversal node
Node temp = root;

//Chunk1 efficiently moves as close as possible to the value we are searching for
//Moves temp to the top of the nodes
while(temp.up != null) {
temp = temp.up;
}
while(temp.down != null) {
while(temp.val < num) {
temp = temp.next;
}
temp = temp.prev;
temp = temp.down;
}
//end of chunk1

//On the bottom node tries to locate the num by traversing the bottom node
while(temp.val != num &&  temp.val != 5001) {

temp = temp.next;
}
//If we find the node return it
if(temp.val == num) {

return temp;
}
//if we dont find the node return root
else {

return root;
}

//Return root if we try to search for a variable
}
return root;
}

//Deletes the Node if it exists
public static int Delete(int num, Node root){

//If we try to delete an illegal number we dont need to search
if(num > 0 || num <= 5000) {
//Searches for the root
Node searchRes = Search(num, root);

//Chunk 1 deletes the node if it is present
//If the node is present in the list
if(searchRes != root) {


Node above = searchRes;
Node prev = searchRes.prev;
Node next = searchRes.next;

//Deletes the nodes by unliniking them and then setting them to null, repeats for any upper nodes
while(above != null) {
prev.next = next;
next.prev = prev;
searchRes = above;
searchRes = null;
above = above.up;
if(above != null) {
prev = above.prev;
next = above.next;
}

}

//Chunk checks to see if we have to delete any upper nodes that no longer house a node between the two infinites
above = root;
while(above.up != null) {
above = above.up;
}
Node down = above;
Node temp = down;
while(down.next.val == 5001 && down.down != null) {

temp = down.down;
temp.up = null;
temp.next.up = null;
down.next = null;
down = null;
down = temp;
}
//end of chunk2
//end of chunk1



return 1;
}
else {
return 0;
}

}
return 0;
}

//Prints the entire List
public static void printAll(Node root, FileWriter output, String[] args)throws IOException{
//traversal node
Node temp = root.next;
Node upBase = root.next;

output.write(" the current Skip List is shown below:\n");
	output.write("---infinity\n");
while(temp.val != 5001) {
	output.write(new Integer(temp.val).toString()+";");
	while(temp.up != null) {
		temp = temp.up;
		output.write(new Integer(temp.val).toString()+";");
	}
	output.write("\n");
	upBase = upBase.next;
	temp = upBase;
	
}
output.write("+++infinity\n");
output.write("---End of Skip List---");

}
}