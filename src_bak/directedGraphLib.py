maxN=256

class directedGraph:
    def __init__(self):
        self.wordMap={}
        self.indexMap={}
        self.graph=[[0 for i in range(maxN)] for j in range(maxN)]
        return
    
    def show(self):
        import networkx as nx
        import matplotlib.pyplot as plt
        G=nx.DiGraph()
        for i in range(maxN):
            for j in range(maxN):
                if self.graph[i][j]>0:
                    G.add_edge(self.indexMap[i],self.indexMap[j],weight=self.graph[i][j])
        pos=nx.layout.bfs_layout(G,self.indexMap[0])
        nx.draw(G,with_labels=True,pos=pos,node_size=80)
        plt.show()
        return
        # import graphviz
        # dot=graphviz.Digraph()
        # for i in range(maxN):
        #     for j in range(maxN):
        #         if self.graph[i][j]>0:
        #             dot.edge(self.indexMap[i],self.indexMap[j],label=str(self.graph[i][j]))
        # # dot.view()
        # # .png
        # dot.render('graph',format='png',cleanup=True)
        # # show png
        # import os
        # os.system('graph.png')
        # return
    
    def addEdge(self,word1,word2):
        word1, word2 = word1.lower(), word2.lower()
        if word1 not in self.wordMap:
            self.wordMap[word1]=len(self.wordMap)
            self.indexMap[len(self.indexMap)]=word1
        if word2 not in self.wordMap:
            self.wordMap[word2]=len(self.wordMap)
            self.indexMap[len(self.indexMap)]=word2
        self.graph[self.wordMap[word1]][self.wordMap[word2]]+=1
        return
    
    def queryBridgeWords(self,word1,word2):
        word1, word2 = word1.lower(), word2.lower()
        if word1 not in self.wordMap or word2 not in self.wordMap:
            return word1 in self.wordMap, word2 in self.wordMap, []
        res=[]
        for i in range(maxN):
            if self.graph[self.wordMap[word1]][i]>0 and self.graph[i][self.wordMap[word2]]>0:
                res.append(self.indexMap[i])
        return True, True, res
    
    def calcShortestPath(self,word1,word2):
        word1, word2 = word1.lower(), word2.lower()
        if word1 not in self.wordMap or word2 not in self.wordMap:
            return []
        dist=[-1 for i in range(maxN)]
        path=[-1 for i in range(maxN)]
        q=[self.wordMap[word1]]
        dist[self.wordMap[word1]]=0
        while len(q)>0:
            u=q.pop(0)
            for i in range(maxN):
                if self.graph[u][i]>0 and dist[i]==-1:
                    dist[i]=dist[u]+1
                    path[i]=u
                    q.append(i)
        res=[]
        if dist[self.wordMap[word2]]==-1:
            return res
        u=self.wordMap[word2]
        while u!=-1:
            res.append(self.indexMap[u])
            u=path[u]
        res.reverse()
        return res
    
    def randomWalk(self,word):
        from random import randint
        word = word.lower()
        if word not in self.wordMap:
            return []
        res=[word]
        graphCopy=[[0 for i in range(maxN)] for j in range(maxN)]
        visEdge=[[0 for i in range(maxN)] for j in range(maxN)]
        for i in range(maxN):
            for j in range(maxN):
                graphCopy[i][j]=self.graph[i][j]
        u=self.wordMap[word] # current node
        while True:
            outEdge=[]
            for i in range(maxN):
                if graphCopy[u][i]: outEdge.append(i)
            if len(outEdge)==0: break
            v=outEdge[randint(0,len(outEdge)-1)]
            res.append(self.indexMap[v])
            if visEdge[u][v]: break
            visEdge[u][v]=1
            u=v
        return res

    def build(self,words):
        for i in range(len(words)-1):
            self.addEdge(words[i],words[i+1])
        return
