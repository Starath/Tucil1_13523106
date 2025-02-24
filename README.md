# IQ Puzzler Pro Solver
By Athian Nugraha Muarajuang (13523106)<br>

This program implements a brute force algorithm to solve the IQ Puzzler Pro game.

## Program Description
The IQ Puzzler Pro Solver finds a solution for placing all puzzle pieces on a board according to the game rules. It supports:
- Default rectangular board configuration
- Custom board configurations

The program uses a backtracking brute force approach to try all possible piece placements, orientations, and reflections until a solution is found or all possibilities are exhausted.

## Requirements
- Java JDK 17 or higher
- Terminal or Command Prompt

## Clone Repo
First, to clone this repository, do:
```bash
git clone https://github.com/Starath/Tucil1_13523106.git
cd Tucil1_13523106
```

## Compilation Instructions
To compile the program, do:
```bash
javac -d bin src/*.java
```

## How to Run
After compilation, do:
```bash
java -cp bin Main
```

2. When prompted, enter the test case file name (.txt). Make sure your test case is in the test folder
3. The program will solve the puzzle and display the solution if one exists
4. You can choose to save the solution to a text file and/or png file when prompted

## Input File Format
The input file should follow this format:
```
N M P
S
puzzle_1_shape
puzzle_2_shape
...
puzzle_P_shape
```
Where:
- N, M: Board dimensions
- P: Number of puzzle pieces
- S: Case type (DEFAULT/CUSTOM)
- For CUSTOM case, the next N lines contain the board configuration<br>
Example:
```
...X...
.XXXXX.
XXXXXXX
.XXXXX.
...X...
```
- Each puzzle piece is represented by its shape using a distinct upper-case character<br>
Example:
```
AA
 A
 BB
BB
B
...
```
And so on

## Author
[Athian Nugraha Muarajuang]
[13523106]