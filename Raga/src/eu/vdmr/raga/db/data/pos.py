infilename = 'musicDBcontent.json'
linecnt = 0
charcnt = 0
zoekpos = 42344
fi = open(infilename, 'r')
for line in fi:
	linecnt += 1
	charcnt += len(line)
	if charcnt > zoekpos:
		print ('probably line ', linecnt)
		exit	
