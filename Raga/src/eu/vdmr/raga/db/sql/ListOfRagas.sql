select * from raga where id in(
select distinct(ragaid) from performance)
order by raga.name;