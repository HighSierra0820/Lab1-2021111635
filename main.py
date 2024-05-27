import directedGraphLib
from random import shuffle
def alphaOnly(string):
    return ''.join(e for e in string if e.isalpha() or e.isspace()).lower()

# string1="never gonna give you up never gonna let you down never gonna run around and desert you never gonna make you cry never gonna say goodbye never gonna tell a lie and hurt you"
# string1=alphaOnly(string1)
# words=string1.split(' ')

# graph=directedGraphLib.directedGraph()
# graph.build(words)
# graph.show()

print("*** DEMO ***")

print("#1")
# 1
string2="To explore strange new worlds, To seek out new life and new civilizations"
string2=alphaOnly(string2)
words=string2.split(' ')
print("building graph...")
graph=directedGraphLib.directedGraph()
graph.build(words)
print()

print("#2")
# 2
graph.show()
print()

print("#3")
# 3
pairs=[("seek", "to"), ("to", "explore"), ("explore", "new"), ("new", "and"), ("and", "exciting"), ("exciting", "synergies")]
for pair in pairs:
    print(pair)
    print('\t', graph.queryBridgeWords(pair[0], pair[1]))
print()

print("#4")
# 4
newStr="Seek to explore new and exciting synergies"
words=newStr.split(' ')
for i in range(len(words)):
    print(words[i], end=' ')
    if i<len(words)-1:
        res=graph.queryBridgeWords(words[i], words[i+1])[2]
        if len(res)>0:
            shuffle(res)
            print(res[0], end=' ')
print('\n')

print("#5")
# 5
pairs=[("to", "and"), ("worlds", "new")]
for pair in pairs:
    print(pair)
    print('\t', graph.calcShortestPath(pair[0], pair[1]))
print()

print("#6")
# 6
for _ in range(5):
    print(graph.randomWalk("to"))
print()
