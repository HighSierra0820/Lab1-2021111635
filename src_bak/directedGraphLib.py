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
        pos=nx.planar_layout(G)
        nx.draw(G,with_labels=True,pos=pos,node_size=80)
        plt.show()
        return
    
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
        # 进入该功能时，程序随机的从图中选择一个节
        # 点，以此为起点沿出边进行随机遍历，记录经
        # 过的所有节点和边，直到出现第一条重复的边
        # 为止，或者进入的某个节点不存在出边为止。
        # 在遍历过程中，用户也可随时停止遍历。
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
