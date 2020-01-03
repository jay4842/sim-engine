import mysqlConnection as sql

def parse_sim_report(lines):
    # parse lines now
    sim_data = []
    sim_count = 0
    for line in lines:
        turn_info = []
        line = line[1:].split(",")
        line = line[0:len(line)-1]
        if(len(line) > 2):
            for idx, step in enumerate(line):
                step = step[1: len(step)-1].split(" ")
                step_count = step[0]
                populationCount = step[1][1:len(step[1])-1]
                avg_population = step[2][1:len(step[2])-1]
                variant_counts = step[3][1:len(step[3])-1]
                details = {
                    'turn_number': idx,
                    'step_number': int(step_count),
                    'population_size': int(populationCount),
                    'avg_population': float(avg_population),
                    'variant_counts': variant_counts
                }
                turn_info.append(details)
                #print(step_info) 
            # now send the turn_info to a query call
            sim_report_query(sim_count, turn_info)
        sim_count += 1

def sim_report_query(sim_count, turn_info):
    #print('making query for ', len(turn_info), ' turns...')
    #print('this will take a second....')
    # first we need to insert into sim_id_refs, so we have a sim_ID to reference
    sim_count_query = 'INSERT INTO sim_id_refs(ID) VALUE({});'.format(sim_count)
    queries = []
    queries.append(sim_count_query)
    for item in turn_info:
        turn = item['turn_number']
        step = item['step_number']
        pop_size = item['population_size']
        avg_pop  = item['avg_population']
        variants = item['variant_counts']
        query = 'INSERT INTO sim_steps(STEP, sim_id, entity_count, avg_entity_count, variant_counts)'\
            ' VALUE({0},{1},{2},{3},\'{4}\');'.format(step,sim_count, pop_size, avg_pop, variants)
        queries.append(query)
    
    sql.execute_many(queries)
    # make query

