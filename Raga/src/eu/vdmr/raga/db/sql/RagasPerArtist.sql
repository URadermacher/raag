select * from raga r where r.id in(
  select distinct(ragaid) 
  from performance p
  join artist a on a.id = p.artistid
  where a.firstname = 'Nikhil'
    and a.lastname = 'Banerjee'
  )
order by r.name;