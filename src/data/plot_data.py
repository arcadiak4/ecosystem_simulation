# encoding: utf-8
"""
documentation sur
https://matplotlib.org/
"""
import matplotlib.pyplot as plt
import matplotlib.gridspec as gridspec
import matplotlib.animation as animation
from matplotlib import style
import csv

## structure d'affichage ##
style.use('bmh')
fig = plt.figure(figsize=(12, 8), constrained_layout=True)
fig.canvas.set_window_title('analyse faune et flore')
spec = gridspec.GridSpec(ncols=2, nrows=4, figure=fig)
ax1 = fig.add_subplot(spec[0, 0])
ax2 = fig.add_subplot(spec[1, 0])
ax3 = fig.add_subplot(spec[2, 0])
ax4 = fig.add_subplot(spec[3, 0])
ax5 = fig.add_subplot(spec[0, 1])
ax6 = fig.add_subplot(spec[1, 1])
ax7 = fig.add_subplot(spec[2, 1])
ax8 = fig.add_subplot(spec[3, 1])

## pie values ##
def make_autopct(values):
	def my_autopct(pct):
		total = sum(values)
		val = int(round(pct*total/100.0))
		return '{p:.2f}%  ({v:d})'.format(p=pct,v=val)
	return my_autopct

## animation ##
def animate(i):
	## file ##
	outfile = open("data.csv", "r")
	file = csv.reader(outfile, delimiter=";")

	## abscisse commune aux plots ##
	xs = []

	## ax1 ##
	y1 = []
	y1b = []
	label1 = 'Flora in temperate'
	## ax2 ##
	y2 = []
	y2b = []
	label2 = 'Flora in desert'
	## ax3 ##
	y3 = []
	y3b = []
	label3 = 'Flora in mountain'
	## ax4 ##
	data4 = []
	colors4 = ['#008000','#cd6090']
	label4 = 'Trees', 'Bushes'
	## ax5 ##
	y5 = []
	y5b = []
	label5 = 'Wildlife in temperate'
	## ax6 ##
	y6 = []
	y6b = []
	y6c = []
	label6 = 'Wildlife in desert'
	## ax7 ##
	y7 = []
	y7b = []
	y7c = []
	label7 = 'Wildlife in Mountain'
	## ax8 ##
	data8 = []
	colors8 = ['#F37735','#5A879F']
	label8 = 'Firefox', 'Iceweasel'
	
	## update plot ##
	for row in file:
		if len(row) > 1:
			## update data ##
			xs.append(int(row[0]))
			y1.append(int(row[1]))
			y1b.append(int(row[2]))
			y2.append(int(row[3]))
			y2b.append(int(row[4]))
			y3.append(int(row[5]))
			y3b.append(int(row[6]))
			data4 = [int(row[15]), int(row[16])]
			y5.append(int(row[9]))
			y5b.append(int(row[10]))
			y6.append(int(row[11]))
			y6b.append(int(row[12]))
			y6c.append(int(row[7]))
			y7.append(int(row[13]))
			y7b.append(int(row[14]))
			y7c.append(int(row[8]))
			data8 = [int(row[17]), int(row[18])]

			## clean plots ##
			ax1.clear()
			ax2.clear()
			ax3.clear()
			ax4.clear()
			ax5.clear()
			ax6.clear()
			ax7.clear()
			ax8.clear()

			## re-draw plots ##
			ax1.plot(xs, y1, label='tree', color='green', linewidth=1.0)
			ax1.plot(xs, y1b, label='bush', color='#cd6090', linewidth=1.0)
			ax1.legend()
			ax1.set_xlabel('iterations')
			ax1.set_ylabel('quantity')
			ax1.set_title(label1)
			
			ax2.plot(xs, y2, label='tree', color='green', linewidth=1.0)
			ax2.plot(xs, y2b, label='bush', color='#cd6090', linewidth=1.0)
			ax2.legend()
			ax2.set_xlabel('iterations')
			ax2.set_ylabel('quantity')
			ax2.set_title(label2)
			

			ax3.plot(xs, y3, label='tree', color='green', linewidth=1.0)
			ax3.plot(xs, y3b, label='bush', color='#cd6090', linewidth=1.0)
			ax3.legend()
			ax3.set_xlabel('iterations')
			ax3.set_ylabel('quantity')
			ax3.set_title(label3)
			
			ax4.pie(data4, autopct=make_autopct(data4), colors=colors4, startangle=90, pctdistance=1.5)
			ax4.axis('equal')
			ax4.set_title("Global flora")
			ax4.legend(label4,loc="center left")

			ax5.plot(xs, y5, label='firefox', color='#F37735', linewidth=1.0)
			ax5.plot(xs, y5b, label='iceweasel', color='#5A879F', linewidth=1.0)
			ax5.legend()
			ax5.set_xlabel('iterations')
			ax5.set_ylabel('quantity')
			ax5.set_title(label5)

			ax6.plot(xs, y6, label='firefox', color='#F37735', linewidth=1.0)
			ax6.plot(xs, y6b, label='iceweasel', color='#5A879F', linewidth=1.0)
			ax6.plot(xs, y6c, '--', label='baby_ff', color='#F37735', linewidth=1.0)
			ax6.legend()
			ax6.set_xlabel('iterations')
			ax6.set_ylabel('quantity')
			ax6.set_title(label6)
			
			ax7.plot(xs, y7, label='firefox', color='#F37735', linewidth=1.0)
			ax7.plot(xs, y7b, label='iceweasel', color='#5A879F', linewidth=1.0)
			ax7.plot(xs, y7c, '--', label='baby_iw', color='#5A879F', linewidth=1.0)
			ax7.legend()
			ax7.set_xlabel('iterations')
			ax7.set_ylabel('quantity')
			ax7.set_title(label7)
			
			ax8.pie(data8, autopct=make_autopct(data8), colors=colors8, startangle=90, pctdistance=1.3)
			ax8.axis('equal')
			ax8.set_title("Global wildlife")
			ax8.legend(label8,loc="center right")
			

ani = animation.FuncAnimation(fig, animate, interval=800)
plt.show()
