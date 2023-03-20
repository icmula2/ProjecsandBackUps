# multiAgents.py
# --------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


from functools import partial
from util import manhattanDistance
from game import Directions
import random, util

from game import Agent
from pacman import GameState
from util import manhattanDistance
from collections import Counter

class ReflexAgent(Agent):
    """
    A reflex agent chooses an action at each choice point by examining
    its alternatives via a state evaluation function.

    The code below is provided as a guide.  You are welcome to change
    it in any way you see fit, so long as you don't touch our method
    headers.
    """


    def getAction(self, gameState: GameState):
        """
        You do not need to change this method, but you're welcome to.

        getAction chooses among the best options according to the evaluation function.

        Just like in the previous project, getAction takes a GameState and returns
        some Directions.X for some X in the set {NORTH, SOUTH, WEST, EAST, STOP}
        """
        # Collect legal moves and successor states
        legalMoves = gameState.getLegalActions()

        # Choose one of the best actions
        scores = [self.evaluationFunction(gameState, action) for action in legalMoves]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices) # Pick randomly among the best

        "Add more of your code here if you want to"

        return legalMoves[chosenIndex]

    def evaluationFunction(self, currentGameState: GameState, action):
        """
        Design a better evaluation function here.

        The evaluation function takes in the current and proposed successor
        GameStates (pacman.py) and returns a number, where higher numbers are better.

        The code below extracts some useful information from the state, like the
        remaining food (newFood) and Pacman position after moving (newPos).
        newScaredTimes holds the number of moves that each ghost will remain
        scared because of Pacman having eaten a power pellet.

        Print out these variables to see what you're getting, then combine them
        to create a masterful evaluation function.
        """
        # Useful information you can extract from a GameState (pacman.py)
        successorGameState = currentGameState.generatePacmanSuccessor(action)
        newPos = successorGameState.getPacmanPosition()
        newFood = successorGameState.getFood()
        newGhostStates = successorGameState.getGhostStates()
        newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]

        "*** YOUR CODE HERE ***"
        distFrmPac = partial(manhattanDistance, newPos)
        closestFood  = 1 / min(map(distFrmPac, newFood.asList()), default = 1000)

        return successorGameState.getScore() + closestFood

def scoreEvaluationFunction(currentGameState: GameState):
    """
    This default evaluation function just returns the score of the state.
    The score is the same one displayed in the Pacman GUI.

    This evaluation function is meant for use with adversarial search agents
    (not reflex agents).
    """
    return currentGameState.getScore()

class MultiAgentSearchAgent(Agent):
    """
    This class provides some common elements to all of your
    multi-agent searchers.  Any methods defined here will be available
    to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

    You *do not* need to make any changes here, but you can if you want to
    add functionality to all your adversarial search agents.  Please do not
    remove anything, however.

    Note: this is an abstract class: one that should not be instantiated.  It's
    only partially specified, and designed to be extended.  Agent (game.py)
    is another abstract class.
    """

    def __init__(self, evalFn = 'scoreEvaluationFunction', depth = '2'):
        self.index = 0 # Pacman is always agent index 0
        self.evaluationFunction = util.lookup(evalFn, globals())
        self.depth = int(depth)

class MinimaxAgent(MultiAgentSearchAgent):
    """
    Your minimax agent (question 2)
    """

    def getAction(self, gameState: GameState):
        """
        Returns the minimax action from the current gameState using self.depth
        and self.evaluationFunction.

        Here are some method calls that might be useful when implementing minimax.

        gameState.getLegalActions(agentIndex):
        Returns a list of legal actions for an agent
        agentIndex=0 means Pacman, ghosts are >= 1

        gameState.generateSuccessor(agentIndex, action):
        Returns the successor game state after an agent takes an action

        gameState.getNumAgents():
        Returns the total number of agents in the game

        gameState.isWin():
        Returns whether or not the game state is a winning state

        gameState.isLose():
        Returns whether or not the game state is a losing state
        """
        "*** YOUR CODE HERE ***"
        
        
        def updateAgentDepth(agentIndex, depth):
            if agentIndex == gameState.getNumAgents() -1 : 
                agentIndex = 0 
                depth -= 1
            else: 
                agentIndex +=1 
            return agentIndex, depth 
        
        def maxValue(gameState, agentIndex, depth): 
            result = Directions.STOP, 0
            agent, depth = updateAgentDepth(agentIndex, depth)
            v = - float ('inf')
            for action in gameState.getLegalActions(agentIndex):
                score = value(gameState.generateSuccessor(agentIndex, action), agent, depth)[1]
                v = max(v, score)
                if v == score:
                    result = action, v 
            return result
        
        def minValue(gameState, agentIndex, depth):
            result = Directions.STOP, 0
            agent, depth = updateAgentDepth(agentIndex, depth)
            v = float ('inf')
            for action in gameState.getLegalActions(agentIndex):
                score = value(gameState.generateSuccessor(agentIndex,action), agent, depth)[1]
                v = min(v, score)
                if v == score:
                    result = action, v 
            return result
        
        def value(gameState, agentIndex , depth): 
            if gameState.isWin() or gameState.isLose() or depth == 0: 
                result = Directions.STOP, self.evaluationFunction(gameState)
            elif agentIndex == 0:
                result = maxValue(gameState, agentIndex, depth)
            else: 
                result = minValue(gameState, agentIndex, depth)
            return result 
        return value(gameState, 0, self.depth)[0]
        util.raiseNotDefined()

class AlphaBetaAgent(MultiAgentSearchAgent):
    """
    Your minimax agent with alpha-beta pruning (question 3)
    """

    def getAction(self, gameState: GameState):
        """
        Returns the minimax action using self.depth and self.evaluationFunction
        """
        "*** YOUR CODE HERE ***"
        def updateAgentDepth(agentIndex, depth):
            if agentIndex == gameState.getNumAgents() -1: 
                agentIndex = 0 
                depth -= 1
            else: 
                agentIndex +=1 
            return agentIndex, depth 
        
        def maxValue(gameState, agentIndex, depth, alpha, beta): 
            result = Directions.STOP, 0
            agent, depth = updateAgentDepth(agentIndex, depth)
            v = - float ('inf')
            for action in gameState.getLegalActions(agentIndex):
                score = value(gameState.generateSuccessor(agentIndex,action), agent, depth, alpha, beta)[1]
                v = max(v, score)
                if v == score:
                    result = action, v 
                if v > beta: 
                    return action, v
                alpha = max(v, alpha)
            return result
        
        def minValue(gameState, agentIndex, depth, alpha, beta):
            result = Directions.STOP, 0
            agent, depth = updateAgentDepth(agentIndex, depth)
            v = float ('inf')
            for action in gameState.getLegalActions(agentIndex):
                print()
                score = value(gameState.generateSuccessor(agentIndex,action), agent, depth, alpha, beta)[1]
                v = min(v, score)
                if v == score:
                    result = action, v 
                if v < alpha: 
                    return action, v
                beta = min(beta, v)
            return result
        
        def value(gameState, agentIndex , depth, alpha, beta): 
            if gameState.isWin() or gameState.isLose() or depth == 0: 
                result = Directions.STOP, self.evaluationFunction(gameState)
            elif agentIndex == 0:
                result = maxValue(gameState, agentIndex, depth, alpha, beta)
            else: 
                result = minValue(gameState, agentIndex, depth, alpha, beta)
            return result 
        return value(gameState, 0, self.depth, -float('inf'), float('inf'))[0]
        util.raiseNotDefined()

class ExpectimaxAgent(MultiAgentSearchAgent):
    """
      Your expectimax agent (question 4)
    """

    def getAction(self, gameState: GameState):
        """
        Returns the expectimax action using self.depth and self.evaluationFunction

        All ghosts should be modeled as choosing uniformly at random from their
        legal moves.
        """
        "*** YOUR CODE HERE ***"
        def updateAgentDepth(agentIndex, depth):
            if agentIndex == gameState.getNumAgents() - 1:
                agentIndex = 0
                depth -= 1
            else:
                agentIndex += 1
            return agentIndex, depth

        def maxValue(gameState, agentIndex, depth, alpha, beta):
            result = Directions.STOP, float('-inf')
            agent, depth = updateAgentDepth(agentIndex, depth)
            v = float('-inf')
            for action in gameState.getLegalActions(agentIndex):
                score = value(gameState.generateSuccessor(agentIndex, action), agent, depth, alpha, beta)[1]
                if score > v:
                    v = score
                    result = action, v
                if v > beta:
                    return action, v
                alpha = max(alpha, v)
            return result

        def eValue(gameState, agentIndex, depth, alpha, beta):
            result = Directions.STOP, 0
            agent, depth = updateAgentDepth(agentIndex, depth)
            v = 0
            numActions = len(gameState.getLegalActions(agentIndex))
            for action in gameState.getLegalActions(agentIndex):
                probability = 1.0 / numActions
                score = value(gameState.generateSuccessor(agentIndex, action), agent, depth, alpha, beta)[1]
                v += probability * score
            return result[0], v

        def value(gameState, agentIndex, depth, alpha, beta):
            if gameState.isWin() or gameState.isLose() or depth == 0:
                return Directions.STOP, self.evaluationFunction(gameState)
            if agentIndex == 0:
                return maxValue(gameState, agentIndex, depth, alpha, beta)
            else:
                return eValue(gameState, agentIndex, depth, alpha, beta)

        return value(gameState, 0, self.depth, float('-inf'), float('inf'))[0]
        util.raiseNotDefined()

def betterEvaluationFunction(currentGameState: GameState):
    """
    Your extreme ghost-hunting, pellet-nabbing, food-gobbling, unstoppable
    evaluation function (question 5).

    DESCRIPTION: <write something here so we know what you did>
    """
    "*** YOUR CODE HERE ***"
    position = currentGameState.getPacmanPosition()
    foodGrid = currentGameState.getFood()
    capsules = currentGameState.getCapsules()
    ghosts = currentGameState.getGhostStates()
    capsulesCount = len(capsules)

    # calculate the distance to the closest dot
    closestDot = float('inf')
    for food in foodGrid.asList():
        closestDot = min(closestDot, manhattanDistance(position, food))

    # calculate the distance to the closest ghost
    closestGhost = float('inf')
    for ghost in ghosts:
        dist = manhattanDistance(position, ghost.getPosition())
        if ghost.scaredTimer > 0:
            dist *= -1  # if the ghost is scared, we want to move towards it
        closestGhost = min(closestGhost, dist)

    # calculate the distance to the closest capsule
    closestCapsule = float('inf')
    for capsule in capsules:
        closestCapsule = min(closestCapsule, manhattanDistance(position, capsule))

    # calculate the score based on various factors
    score = currentGameState.getScore()
    if closestGhost < 2:
        score -= 100
    score += 1.0 / closestDot
    score += 2.0 / (capsulesCount + 1)
    score += 1.0 / (closestCapsule + 1)
    score += closestGhost

    return score

    util.raiseNotDefined()

# Abbreviation
better = betterEvaluationFunction