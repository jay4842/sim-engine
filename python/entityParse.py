# TODO parse entity logs
# TODO store entity info to a log at runtime
import mysqlConnection as sql
import time

def parse_entity_report(lines, file_name):
    print('entity report: {}'.format(file_name))
    if(len(lines) < 5):
        #print('Not enough lines for this report -> {}'.format(file_name))
        return
    
    steps = []
    queries = []
    sim_id = int(file_name.split('/')[-1].split('_')[-1].split('.')[0]) + 1
    print('\nLinked to sim # {}'.format(sim_id))
    return
    for idx, line in enumerate(lines):
    
        line = line.split(' ')[1]
        line = line[0:len(line)-1].split(',')
        for sub in line:
            sub = sub.split('_')
            query = make_single_entity_query(sim_id, (idx*30), sub)
            queries.append(query)

    #for query in queries:
    #    print(query)
    sql.execute_many(queries)
    #time.sleep(2.5) # give it a rest

# takes info for one entity and create a query for it
def make_single_entity_query(sim_id, step, entity_info):
    # entity_info = [id, y, x, pos]
    entity_id = entity_info[0]
    gps = '_'.join(entity_info[1:])
    query = 'INSERT INTO entity_info_at_step(step_id, sim_id, entity_id, gps)'\
        ' VALUE({0}, {1}, {2}, \'{3}\');'.format(step, sim_id, entity_id, gps)
    return query



    