import directedGraphLib
from random import shuffle
def alphaOnly(string):
    return ''.join(e for e in string if e.isalpha() or e.isspace()).lower()

string0='''Software Engineering - Lab 1
2021111635'''

string1='''Menu:
    1. Read text from file & build graph
    2. Show the graph
    3. Query bridge words
    4. Generate new text with bridge words
    5. Calculate shortest path
    6. Random walk
                                    0. Exit
'''

if __name__=="__main__":
    graph=None
    
    print(string0)
    while True:
        print()
        print(string1)
        choice=input("Enter your choice: ")
        
        if choice=='1':
            filename=input("Enter filename: ")
            with open(filename, 'r') as f:
                string=f.read()
            string=alphaOnly(string)
            while '  ' in string:
                string=string.replace('  ', ' ')
            words=string.split(' ')
            del graph
            graph=directedGraphLib.directedGraph()
            graph.build(words)
            print("Graph built.")
        
        elif choice=='2':
            graph.show()
            print("Graph shown.")
        
        elif choice=='3':
            word1=input("Enter word 1: ")
            word2=input("Enter word 2: ")
            exists1, exists2, res=graph.queryBridgeWords(word1, word2)
            if exists1 and exists2:
                if len(res)>0:
                    print(f"Bridge words between '{word1}' and '{word2}':", ', '.join(res))
                else:
                    print(f"Bridge words between '{word1}' and '{word2}' not found.")
            elif not exists1 and not exists2:
                print(f"Words '{word1}' and '{word2}' not found.")
            elif not exists1:
                print(f"Word '{word1}' not found.")
            else:
                print(f"Word '{word2}' not found.")
        
        elif choice=='4':
            newStr=input("Enter string: ")
            words=newStr.split(' ')
            print("New string:")
            for i in range(len(words)):
                print(words[i], end=' ')
                if i<len(words)-1:
                    res=graph.queryBridgeWords(words[i], words[i+1])[2]
                    if len(res)>0:
                        shuffle(res)
                        print(res[0], end=' ')
            print()
        
        elif choice=='5':
            word1=input("Enter word 1: ")
            word2=input("Enter word 2: ")
            res=graph.calcShortestPath(word1, word2)
            if len(res)>0:
                print("Shortest path:", '->'.join(res))
            else:
                print("Shortest path not found: unreachable.")
        
        elif choice=='6':
            word=input("Enter word: ")
            res=graph.randomWalk(word)
            print("Random walk:", '->'.join(res))
        
        elif choice=='0':
            break
        
        else:
            print("Invalid choice.")
    
    print("Exiting...")
    