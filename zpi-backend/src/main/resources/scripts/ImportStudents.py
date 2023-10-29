import pandas as pd
import json


def capitalize_surname(surname: str) -> str:
    if pd.notna(surname):
        words = surname.split('-')
        words = [word.capitalize() for word in words]
        return '-'.join(words)
    else:
        return surname 
    

def create_student_mail(index: str):
    if index:
        return str(index) + '@student.pwr.edu.pl'
    else:
        return index


def merge_rows_json(json: list[dict]) -> list[dict]:
    merged_data = {}
    for entry in json:
        mail = entry["mail"]
        programsCycles = tuple(entry["programsCycles"])
        
        if mail not in merged_data:
            merged_data[mail] = entry
            merged_data[mail]["programsCycles"] = [programsCycles]
        else:
            if programsCycles not in merged_data[mail]["programsCycles"]:
                merged_data[mail]["programsCycles"].append(programsCycles)
    
    return list(merged_data.values())


def merge_full_json(data: dict[str, list[dict[str, any]]]) -> dict[str, list[dict[str, any]]]:
    # Create a dictionary to store the merged data
    merged_data = {}
    
    for key, value in data.items():
        # Check if the value is a list of dictionaries
        if isinstance(value, list) and all(isinstance(entry, dict) for entry in value):
            # Apply the merge_rows_json function to the list
            merged_data[key] = merge_rows_json(value)
        else:
            # If the value is not a list of dictionaries, keep it as-is
            merged_data[key] = value

    return merged_data


def read_file(file_path: str):
    data_types = {
        'INDEKS': str,  
        'NAZWISKO': str,
        'IMIE': str,
        'PROGRAM': str,
        'CYKL_DYDAKTYCZNY': str,
        'STATUS': str,
        'DATA_PRZYJECIA': str,
        'DATA_ROZPOCZECIA': str,
        'ETAP': str,
    }
    try:
        if file_path.lower().endswith('.csv'):
            df = pd.read_csv(file_path, dtype=data_types)
        elif file_path.lower().endswith('.xlsx'):
            df = pd.read_excel(file_path, dtype=data_types)
        else:
            raise ValueError("Unsupported file format")
    
        required_columns = ["INDEKS", "NAZWISKO", "IMIE", "PROGRAM", "CYKL_DYDAKTYCZNY", 
                            "STATUS", "ETAP"]
        missing_columns = [col for col in required_columns if col not in df.columns]
        if missing_columns:
            raise ValueError(f"Missing columns: {', '.join(missing_columns)}")
        df.drop(columns=[col for col in df.columns if col not in required_columns], inplace=True)

        df.rename(columns = {'NAZWISKO' :'surname', 'IMIE' :'name',
                              'INDEKS': 'index', 'STATUS': 'status'}, inplace = True)
        
        df = df.apply(lambda x: x.str.strip() if x.dtype == "object" else x)

        df['surname'] = df['surname'].apply(capitalize_surname)
        df['name'] = df['name'].str.capitalize()
        df['PROGRAM'] = df['PROGRAM'].str.upper()
        df['CYKL_DYDAKTYCZNY'] = df['CYKL_DYDAKTYCZNY'].str.upper()
        df['status'] = df['status'].str.upper()
        df['ETAP'] = df['ETAP'].str.upper()

        df['programsCycles'] = list(zip(df['PROGRAM'], df['CYKL_DYDAKTYCZNY']))
        df['mail'] = df['index'].apply(create_student_mail)
        df['role'] = 'student'

        df = df.fillna('')

        df = df.reindex(columns=['mail', 'name', \
                                 'surname', 'index',\
                                'status', 'role', 'programsCycles',\
                                'PROGRAM', 'CYKL_DYDAKTYCZNY', 'ETAP'])
        print(df.columns)


        # for index, row in df.iterrows():
        #     print(row['programsCycles'])

        invalid_index_rows = df[~df["index"].astype(str).str.match(r'^\d{6}$')]
        invalid_surname_rows = df[~df["surname"].astype(str).str.match(r'^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ðśŚćĆżŻźŹńŃłŁąĄęĘóÓ ,.\'-]{1,50}$')]
        invalid_name_rows = df[~df["name"].astype(str).str.match(r'^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ðśŚćĆżŻźŹńŃłŁąĄęĘóÓ ,.\'-]{1,50}$')]
        invalid_program_rows = df[~df["PROGRAM"].astype(str).str.match(r"^[A-Z0-9]{1,5}-[A-Z]{1,5}-[A-Z0-9]{1,5}-[A-Z0-9]{1,6}$")]
        invalid_teaching_cycle_rows = df[~df["CYKL_DYDAKTYCZNY"].astype(str).str.match(r"^\d{4}/\d{2}-[A-Z]{1,3}$")]
        # invalid_teaching_cycle_rows = df[~df["CYKL_DYDAKTYCZNY"].astype(str).str.match(r"^\d{4}/\d{2}(?:-[A-Z]{1,3})?$")]
        invalid_status_rows = df[~df["status"].astype(str).str.match(r"^[A-Z]{1,5}$")]
        # invalid_stage_rows = df[~df["ETAP"].astype(str).str.match(r'^[A-Z0-9]{1,5}-[A-Z]{1,5}-\d{1,5}$')]

       
        df_valid = df[~df['index'].isin(invalid_index_rows['index'])]
        df_valid = df_valid[~df_valid['surname'].isin(invalid_surname_rows['surname'])]
        df_valid = df_valid[~df_valid['name'].isin(invalid_name_rows['name'])]
        df_valid = df_valid[~df_valid['PROGRAM'].isin(invalid_program_rows['PROGRAM'])]
        df_valid = df_valid[~df_valid['CYKL_DYDAKTYCZNY'].isin(invalid_teaching_cycle_rows['CYKL_DYDAKTYCZNY'])]
        df_valid = df_valid[~df_valid['status'].isin(invalid_status_rows['status'])]
        # df_valid = df_valid[~df_valid.index.isin(invalid_stage_rows.index)]

        df.drop(['PROGRAM', 'CYKL_DYDAKTYCZNY', 'ETAP'], axis=1, inplace=True)
        df_valid = df_valid.drop(['PROGRAM', 'CYKL_DYDAKTYCZNY', 'ETAP'], axis=1)
        invalid_index_rows = invalid_index_rows.drop(['PROGRAM', 'CYKL_DYDAKTYCZNY', 'ETAP'], axis=1)
        invalid_surname_rows = invalid_surname_rows.drop(['PROGRAM', 'CYKL_DYDAKTYCZNY', 'ETAP'], axis=1)
        invalid_name_rows = invalid_name_rows.drop(['PROGRAM', 'CYKL_DYDAKTYCZNY', 'ETAP'], axis=1)
        invalid_program_rows = invalid_program_rows.drop(['PROGRAM', 'CYKL_DYDAKTYCZNY', 'ETAP'], axis=1)
        invalid_teaching_cycle_rows = invalid_teaching_cycle_rows.drop(['PROGRAM', 'CYKL_DYDAKTYCZNY', 'ETAP'], axis=1)
        invalid_status_rows = invalid_status_rows.drop(['PROGRAM', 'CYKL_DYDAKTYCZNY', 'ETAP'], axis=1)
        # invalid_stage_rows = invalid_stage_rows.drop(['PROGRAM', 'CYKL_DYDAKTYCZNY', 'ETAP'], axis=1)

                
        return df_valid, invalid_index_rows, invalid_surname_rows,\
                invalid_name_rows, invalid_program_rows, invalid_teaching_cycle_rows,\
                invalid_status_rows #, invalid_stage_rows 
    except pd.errors.ParserError as e:
        raise ValueError("Error parsing the file. Please check the file format and structure.") from e
    

def dataframes_to_json(df_valid, invalid_index_rows, invalid_surname_rows,\
                invalid_name_rows, invalid_program_rows, invalid_teaching_cycle_rows,\
                invalid_status_rows) -> str:    # , invalid_stage_rows) -> str: 
    try:
        df_valid_json = df_valid.to_json(orient='records')
        invalid_index_json = invalid_index_rows.to_json(orient='records')
        invalid_surname_json = invalid_surname_rows.to_json(orient='records')
        invalid_name_json = invalid_name_rows.to_json(orient='records')
        invalid_program_json = invalid_program_rows.to_json(orient='records')
        invalid_teaching_cycle_json = invalid_teaching_cycle_rows.to_json(orient='records')
        invalid_status_json = invalid_status_rows.to_json(orient='records')
        # invalid_stage_json = invalid_stage_rows.to_json(orient='records')

        full_json = {
            'valid_data': json.loads(df_valid_json),
            'invalid_indices': json.loads(invalid_index_json),
            'invalid_surnames': json.loads(invalid_surname_json),
            'invalid_names': json.loads(invalid_name_json),
            'invalid_programs': json.loads(invalid_program_json),
            'invalid_teaching_cycles': json.loads(invalid_teaching_cycle_json),
            'invalid_statuses': json.loads(invalid_status_json),
            # 'invalid_stages': json.loads(invalid_stage_json)
        }

        full_json = merge_full_json(full_json)
        output = json.dumps(full_json, indent=3, allow_nan=True)
        print(output)

        return output 
    except json.JSONDecodeError as jserr:
        print(f"Error occured while decoding JSON: {str(jserr)}")



def main():
    file_path = "src/test/resources/ZPI_dane.xlsx" 
    # file_path = "src/test/resources/ZPI_dane (1).xlsx" 

    try:
        df_valid, invalid_index_rows, invalid_surname_rows,\
            invalid_name_rows, invalid_program_rows, invalid_teaching_cycle_rows,\
            invalid_status_rows  = read_file(file_path) # , invalid_stage_rows  = read_file(file_path) 
        
        dataframes_to_json(df_valid, invalid_index_rows, invalid_surname_rows,\
            invalid_name_rows, invalid_program_rows, invalid_teaching_cycle_rows,\
            invalid_status_rows)    # , invalid_stage_rows) 

    except ValueError as e:
        print(f"Error: {str(e)}")

if __name__ == '__main__':
    main()