import chardet
import random

names_list = []
full_list = []
temp_list = []

# open the file in binary mode and read it to detect the encoding
with open('names_for_3.1.2.3.txt', 'rb') as file:
    result = chardet.detect(file.read())
    encoding = result['encoding']

# open and read the file with the correct encoding and then transfer its content to a 2D table
with open('names_for_3.1.2.3.txt', 'r', encoding=encoding) as file:
    for line in file:
        names = line.strip().split('\t')
        names_list.append(names)
        full_list.append(names)

# perform cyclic rotation on the last names
for i in range(5):
    previous_last_name = names_list[0][1]
    names_list[0][1] = names_list[9999][1]
    last_name = []

    for j in range(1, len(names_list)):
        last_name = names_list[j][1]
        names_list[j][1] = previous_last_name
        previous_last_name = last_name

    full_list = full_list + names_list[:]

# add the index, trip_offer_code and deposit amount in the names of the list
mod_list = []
for index, sublist in enumerate(full_list, start=1):
    mod_sublist = [index] + sublist

    if index <= 20000:
        trip_offer_code = 8001
    elif 20000 < index <= 40000:
        trip_offer_code = 8002
    elif index > 40000:
        trip_offer_code = 8003

    mod_sublist.append(trip_offer_code)
    mod_sublist.append(random.randint(50, 200))

    mod_list.append(mod_sublist)

# bring the data into proper form for insertion to the reservation_offers table and save them into a txt
formatted_data = ',\n'.join(['(' + ', '.join(map(repr, sublist)) + ')' for sublist in mod_list])

formatted_data = "INSERT INTO reservation_offers VALUES\n" + formatted_data + ";"

with open("reservation_offers_names.txt", "w") as output:
    output.write(str(formatted_data))