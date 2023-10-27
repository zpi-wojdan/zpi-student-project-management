import pandas as pd
import json


def capitalize_surname(surname: str) -> str:
    if pd.notna(surname):
        words = surname.split('-')
        words = [word.capitalize() for word in words]
        return '-'.join(words)
    else:
        return surname 



def read_file(file_path: str):
    try:
        if file_path.lower().endswith('.csv'):
            df = pd.read_csv(file_path)
        elif file_path.lower().endswith('.xlsx'):
            df = pd.read_excel(file_path)#, parse_dates=["DATA_PRZYJECIA", "DATA_ROZPOCZECIA"], date_parser=date_parser)
        else:
            raise ValueError("Unsupported file format")
    
        required_columns = ["INDEKS", "NAZWISKO", "IMIE", "PROGRAM", "CYKL_DYDAKTYCZNY", 
                            "STATUS", "ETAP"] # "DATA_PRZYJECIA", "DATA_ROZPOCZECIA",
        missing_columns = [col for col in required_columns if col not in df.columns]
        if missing_columns:
            raise ValueError(f"Missing columns: {', '.join(missing_columns)}")
        
        # #   convert to datetime if possible, ignore errors and leave non-convertible values as they are
        # df['DATA_PRZYJECIA'] = pd.to_datetime(df['DATA_PRZYJECIA'], errors='coerce')
        # df['DATA_ROZPOCZECIA'] = pd.to_datetime(df['DATA_ROZPOCZECIA'], errors='coerce')
        # #   format datetime columns to string in the desired format
        # df['DATA_PRZYJECIA'] = df['DATA_PRZYJECIA'].dt.strftime("%d.%m.%y")
        # df['DATA_ROZPOCZECIA'] = df['DATA_ROZPOCZECIA'].dt.strftime("%d.%m.%y")

        df['NAZWISKO'] = df['NAZWISKO'].apply(capitalize_surname)
        df['IMIE'] = df['IMIE'].str.capitalize()
        df['PROGRAM'] = df['PROGRAM'].str.upper()
        df['CYKL_DYDAKTYCZNY'] = df['CYKL_DYDAKTYCZNY'].str.upper()
        df['STATUS'] = df['STATUS'].str.upper()
        df['ETAP'] = df['ETAP'].str.upper()
        
        #   picking invalid rows from the original dataframe through regex expressions
        invalid_index_rows = df[~df["INDEKS"].astype(str).str.match(r'^\d{6}$')]
        invalid_surname_rows = df[~df["NAZWISKO"].astype(str).str.match(r'^[A-Za-zĄĆĘŁŃÓŚŹŻąćęłńóśźż\s-]{0,50}$')]
        invalid_name_rows = df[~df["IMIE"].astype(str).str.match(r'^[A-Za-zĄĆĘŁŃÓŚŹŻąćęłńóśźż\s-]{0,50}$')]
        invalid_program_rows = df[~df["PROGRAM"].astype(str).str.match(r"^[A-Z0-9]{1,5}-[A-Z]{1,5}-[A-Z0-9]{1,5}-[A-Z0-9]{1,6}$")]
        invalid_teaching_cycle_rows = df[~df["CYKL_DYDAKTYCZNY"].astype(str).str.match(r"^\d{4}/\d{2}-[A-Z]{1,3}$")]
        invalid_status_rows = df[~df["STATUS"].astype(str).str.match(r"^[A-Z]{1,5}$")]
        # invalid_admission_date_rows = df[~df["DATA_PRZYJECIA"].astype(str).str.match(r'^(0[1-9]|[12][0-9]|3[01])\.(0[1-9]|1[012])\.\d{2}$')]
        # invalid_start_date_rows = df[~df["DATA_ROZPOCZECIA"].astype(str).str.match(r'^(0[1-9]|[12][0-9]|3[01])\.(0[1-9]|1[012])\.\d{2}$')]
        invalid_stage_rows = df[~df["ETAP"].astype(str).str.match(r'^[A-Z0-9]{1,5}-[A-Z]{1,5}-\d{1,5}$')]

        #   filtering the original dataframe based on the lists with invalid rows
        df_valid = df[~df.index.isin(invalid_index_rows.index)]
        df_valid = df_valid[~df_valid.index.isin(invalid_surname_rows.index)]
        df_valid = df_valid[~df_valid.index.isin(invalid_name_rows.index)]
        df_valid = df_valid[~df_valid.index.isin(invalid_program_rows.index)]
        df_valid = df_valid[~df_valid.index.isin(invalid_teaching_cycle_rows.index)]
        df_valid = df_valid[~df_valid.index.isin(invalid_status_rows.index)]
        # df_valid = df_valid[~df_valid.index.isin(invalid_admission_date_rows.index)]
        # df_valid = df_valid[~df_valid.index.isin(invalid_start_date_rows.index)]
        df_valid = df_valid[~df_valid.index.isin(invalid_stage_rows.index)]

                
        return df_valid, invalid_index_rows, invalid_surname_rows,\
                invalid_name_rows, invalid_program_rows, invalid_teaching_cycle_rows,\
                invalid_status_rows, invalid_stage_rows #invalid_admission_date_rows, invalid_start_date_rows,
    except pd.errors.ParserError as e:
        raise ValueError("Error parsing the file. Please check the file format and structure.") from e
    

def dataframes_to_json(df_valid, invalid_index_rows, invalid_surname_rows,\
                invalid_name_rows, invalid_program_rows, invalid_teaching_cycle_rows,\
                invalid_status_rows, invalid_stage_rows) -> str:#invalid_admission_date_rows, invalid_start_date_rows, 
    try:
        df_valid_json = df_valid.to_json(orient='records')
        invalid_index_json = invalid_index_rows.to_json(orient='records')
        invalid_surname_json = invalid_surname_rows.to_json(orient='records')
        invalid_name_json = invalid_name_rows.to_json(orient='records')
        invalid_program_json = invalid_program_rows.to_json(orient='records')
        invalid_teaching_cycle_json = invalid_teaching_cycle_rows.to_json(orient='records')
        invalid_status_json = invalid_status_rows.to_json(orient='records')
        # invalid_admission_date_json = invalid_admission_date_rows.to_json(orient='records')
        # invalid_start_date_json = invalid_start_date_rows.to_json(orient='records')
        invalid_stage_json = invalid_stage_rows.to_json(orient='records')

        full_json = {
            'valid_data': json.loads(df_valid_json),
            'invalid_indices': json.loads(invalid_index_json),
            'invalid_surnames': json.loads(invalid_surname_json),
            'invalid_names': json.loads(invalid_name_json),
            'invalid_programs': json.loads(invalid_program_json),
            'invalid_teaching_cycles': json.loads(invalid_teaching_cycle_json),
            'invalid_statuses': json.loads(invalid_status_json),
            # 'invalid_admission_dates': json.loads(invalid_admission_date_json),
            # 'invalid_start_dates': json.loads(invalid_start_date_json),
            'invalid_stages': json.loads(invalid_stage_json)
        }

        output = json.dumps(full_json, indent=3, allow_nan=True)
        return output 
    except json.JSONDecodeError as jserr:
        print(f"Error occured while decoding JSON: {str(jserr)}")



def main():
    file_path = "src/test/resources/ZPI_dane.xlsx" 
    try:
        df_valid, invalid_index_rows, invalid_surname_rows,\
            invalid_name_rows, invalid_program_rows, invalid_teaching_cycle_rows,\
            invalid_status_rows, invalid_stage_rows  = read_file(file_path) #invalid_admission_date_rows, invalid_start_date_rows, 
        
        dataframes_to_json(df_valid, invalid_index_rows, invalid_surname_rows,\
            invalid_name_rows, invalid_program_rows, invalid_teaching_cycle_rows,\
            invalid_status_rows, invalid_stage_rows) #invalid_admission_date_rows, invalid_start_date_rows, 

    except ValueError as e:
        print(f"Error: {str(e)}")

if __name__ == '__main__':
    main()