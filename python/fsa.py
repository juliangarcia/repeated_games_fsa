from collections import namedtuple
import scipy.misc
import os
import pydot
import re
import matplotlib.pyplot as plt
from IPython.core.display import Image

#defines a state as a named tuple
State = namedtuple('State', 'action DD CD DC CC')


def find_between(s, pre, post=''):
	"""
	Finds a substring in s, between pre and post
	"""
	result = re.search(pre+'(.*)'+post, s)
	return result.group(1)

def parse_string_to_automata(string):
	"""
	Given a string representation of an aumata, returns a list of states
	"""
	list_of_states = []
	no_rubish = find_between(string, '\[', '\]')
	for i in no_rubish.split(','):
		state_strings = find_between(i, '/').split(' ')
		list_of_states.append(State(i.strip()[0], int(state_strings[0]), int(state_strings[1]), int(state_strings[2]), int(state_strings[3])))
	return list_of_states


# def draw_automata(list_of_states, title='', size=5):
# 	graph = pydot.Dot(graph_type='digraph')
# 	list_of_nodes = []
# 	#add nodes
# 	for i, state in enumerate(list_of_states):
# 		if state.action == 'C':
# 			color = 'blue'
#         if state.action == 'D':
#         	color = 'red'
#         list_of_nodes.append(pydot.Node(str(i), style="filled", fillcolor=color))
# 	for i in list_of_nodes:
# 		graph.add_node(i)
#     #add transitions
# 	for i, state in enumerate(list_of_states):
# 		graph.add_edge(pydot.Edge(i, state.DD, fontsize="10.0", label='DD'))
# 		graph.add_edge(pydot.Edge(i, state.DC, fontsize="10.0", label='DC'))
# 		graph.add_edge(pydot.Edge(i, state.CD, fontsize="10.0", label='CD'))
# 		graph.add_edge(pydot.Edge(i, state.CC, fontsize="10.0", label='CC'))
# 	graph.write_png('automata.png')
# 	return graph
# 	plt.figure(figsize=(size, size))
# 	plt.xticks([],[])
# 	plt.yticks([],[])
# 	plt.imshow(scipy.misc.imread('automata.png'))
# 	plt.title(title)
# 	os.remove('automata.png')

