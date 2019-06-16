select * from artist where id in 
	( select distinct(artistid) from performanceartist pa
	    join Instrument i on i.id = pa.instrumentid
	    where i.name = 'Sitar'); 
