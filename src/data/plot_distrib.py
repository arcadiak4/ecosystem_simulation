# encoding: utf-8
"""
documentation sur
https://matplotlib.org/
"""
import matplotlib.pyplot as plt
import csv

# ouverture fichier csv
outfile = open("biomes_data.csv", "r")
file = csv.reader(outfile, delimiter=';')

# création des données de la pie chart
biomes = []
explode = []
for row in file:
	biomes.append(row[0])
	biomes.append(row[1])
	biomes.append(row[2])
	biomes.append(row[3])

# met en avant le(s) plus gros biome(s) #
bigger = max(biomes)
for val in biomes:
	if (val == bigger):
		explode.append(0.05)
	else:
		explode.append(0)

# paramétrage de la pie chart #
labels = 'Ocean', 'Temperate', 'Desert', 'Mountain'
colors = ['#0000FF','#228B22','#DAA520','#778899']

fig, ax = plt.subplots()
fig.canvas.set_window_title('distribution des biomes')

ax.pie(biomes, explode=explode, labels=labels, colors=colors, autopct='%1.0f%%',
        shadow=True, startangle=90)
ax.axis('equal')
ax.set_title("Distribution des biomes")

plt.show()
