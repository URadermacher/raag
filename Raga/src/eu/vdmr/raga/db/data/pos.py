infilename = 'musicDBcontent.json'
print('hello')
linecnt = 0
charcnt = 0
zoekpos = 71909
print('open')
fi = open(infilename, 'r')
print('opened')
for line in fi:
	linecnt += 1
	print('cnt', linecnt)
	charcnt += len(line)
	if charcnt > zoekpos:
		print ('probably line ', linecnt)
		exit	
print('done')