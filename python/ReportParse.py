import argparse
import sys

import simParse

parser = argparse.ArgumentParser(description='Process a report')
parser.add_argument('--input_file', dest='input_file', default='/logs/simReports/sim_report.txt', help='input file path')
parser.add_argument('--report_type', dest='report_type', default='sim_report', help='input file type')
args = parser.parse_args()

def parse_file(input_path, type_of_report):
    print("parsing: " + input_path)
    print("Type   : " + type_of_report)
    lines = []

    ''' a sim_report is a detailed report of population information for each turn '''
    if(type_of_report is 'sim_report'):
        print('creating sim report parsed file')
        with open(input_path, 'r') as file:
            print(file)
            for line in file:
                lines.append(line)
            #
            file.close()
        
        simParse.parse_sim_report(lines)

'''
MAIN
'''
if __name__ == "__main__":
    path = sys.path[0]
    path = path.split('/')
    path = "/".join(path[0:len(path)-1])
    parse_file(path + args.input_file, args.report_type)