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
            df = pd.read_excel(file_path)
        else:
            raise ValueError("Unsupported file format")
    
        required_columns = ["Lp.", "Tytuł/stopień", "Nazwisko", "Imię",	"Jednostka",
                             "Podjednostka", "Stanowisko", "Telefon", "E-mail"]

        missing_columns = [col for col in required_columns if col not in df.columns]
        if missing_columns:
            raise ValueError(f"Missing columns: {', '.join(missing_columns)}")  

        df = df.fillna('')    
        df = df.rename(columns={'Lp.': 'id', 'Tytuł/stopień': 'title', 'Nazwisko': 'surname',
                                'Imię': 'name', 'Jednostka': 'faculty',
                                'Podjednostka': 'department', 'Stanowisko': 'position',
                                'Telefon': 'phone_number', 'E-mail': 'mail'})

        df["title"] = df['title'].str.lower()
        df['surname'] = df['surname'].apply(capitalize_surname)
        df['name'] = df['name'].str.capitalize()
        df['faculty'] = df['faculty'].str.upper()
        df['department'] = df['department'].str.upper()
        df['position'] = df['position'].str.lower()
        df['mail'] = df['mail'].str.lower()    

        #   picking invalid rows from the original dataframe through regex expressions
        invalid_index_rows = df[~df["id"].astype(str).str.match(r'^\d{1,5}$')]
        invalid_academic_title_rows = df[~df["title"].astype(str).str.match(r'^[a-z. ]{0,10}$')]
        invalid_surname_rows = df[~df["surname"].astype(str).str.match(r'^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ðśŚćĆżŻźŹńŃłŁąĄęĘóÓ ,.\'-]{1,50}$')]
        invalid_name_rows = df[~df["name"].astype(str).str.match(r'^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ðśŚćĆżŻźŹńŃłŁąĄęĘóÓ ,.\'-]{1,50}$')]
        invalid_unit_rows = df[~df["faculty"].astype(str).str.match(r'[A-Z0-9]{3,4}')]
        invalid_subunit_rows = df[~df["department"].astype(str).str.match(r"^[A-Z0-9/]{1,10}$")]
        invalid_position_rows = df[~df["position"].astype(str).str.match(r'^[a-z0-9zĄĆĘŁŃÓŚŹŻąćęłńóśźż\s()\-]{1,50}$')]
        invalid_phone_number_rows = df[~df["phone_number"].astype(str).str.match(r'^(?:([+]?[\s0-9]+)?(\d{3}|[(]?[0-9]+[)])?([-]?[\s]?[0-9])+$|$)')]
        invalid_email_rows = df[~df["mail"].astype(str).str.match(r'^[a-z0-9-]{1,50}(\.[a-z0-9-]{1,50}){0,4}@(?:student\.)?(pwr\.edu\.pl|pwr\.wroc\.pl)$')]


        #   filtering the original dataframe based on the lists with invalid rows
        df_valid = df[~df.index.isin(invalid_index_rows.index)]
        df_valid = df_valid[~df_valid.index.isin(invalid_academic_title_rows.index)]
        df_valid = df_valid[~df_valid.index.isin(invalid_surname_rows.index)]
        df_valid = df_valid[~df_valid.index.isin(invalid_name_rows.index)]
        df_valid = df_valid[~df_valid.index.isin(invalid_unit_rows.index)]
        df_valid = df_valid[~df_valid.index.isin(invalid_subunit_rows.index)]
        df_valid = df_valid[~df_valid.index.isin(invalid_position_rows.index)]
        df_valid = df_valid[~df_valid.index.isin(invalid_phone_number_rows.index)]
        df_valid = df_valid[~df_valid.index.isin(invalid_email_rows.index)]
                
        return df_valid, invalid_index_rows, invalid_academic_title_rows,\
                invalid_surname_rows, invalid_name_rows, invalid_unit_rows,\
                invalid_subunit_rows, invalid_position_rows,\
                invalid_phone_number_rows, invalid_email_rows
    except pd.errors.ParserError as e:
        raise ValueError("Error parsing the file. Please check the file format and structure.") from e
    

def dataframes_to_json(df_valid, invalid_index_rows, invalid_academic_title_rows,\
                invalid_surname_rows, invalid_name_rows, invalid_unit_rows,\
                invalid_subunit_rows, invalid_position_rows,\
                invalid_phone_number_rows, invalid_email_rows) -> str:
    try:
        df_valid_json = df_valid.to_json(orient='records')
        invalid_index_json = invalid_index_rows.to_json(orient='records')
        invalid_academic_title_json = invalid_academic_title_rows.to_json(orient='records')
        invalid_surname_json = invalid_surname_rows.to_json(orient='records')
        invalid_name_json = invalid_name_rows.to_json(orient='records')
        invalid_unit_json = invalid_unit_rows.to_json(orient='records')
        invalid_subunit_json = invalid_subunit_rows.to_json(orient='records')
        invalid_position_json = invalid_position_rows.to_json(orient='records')
        invalid_phone_number_json = invalid_phone_number_rows.to_json(orient='records')
        invalid_email_json = invalid_email_rows.to_json(orient='records')

        full_json = {
            'valid_data': json.loads(df_valid_json),
            'invalid_indices': json.loads(invalid_index_json),
            'invalid_academic_titles': json.loads(invalid_academic_title_json),
            'invalid_surnames': json.loads(invalid_surname_json),
            'invalid_names': json.loads(invalid_name_json),
            'invalid_units': json.loads(invalid_unit_json),
            'invalid_subunits': json.loads(invalid_subunit_json),
            'invalid_positions': json.loads(invalid_position_json),
            'invalid_phone_numbers': json.loads(invalid_phone_number_json),
            'invalid_emails': json.loads(invalid_email_json)
        }

        output = json.dumps(full_json, indent=3, allow_nan=True)
        # print(output)
        return output
    except json.JSONDecodeError as jserr:
        print(f"Error occured while decoding JSON: {str(jserr)}")


def main():
    file_path = "src/test/resources/pracownicy-1.xlsx" 
    try:
        df_valid, invalid_index_rows, invalid_academic_title_rows,\
            invalid_surname_rows, invalid_name_rows, invalid_unit_rows,\
            invalid_subunit_rows, invalid_position_rows,\
            invalid_phone_number_rows, invalid_email_rows  = read_file(file_path)

        dataframes_to_json(df_valid, invalid_index_rows, invalid_academic_title_rows,\
            invalid_surname_rows, invalid_name_rows, invalid_unit_rows,\
            invalid_subunit_rows, invalid_position_rows,\
            invalid_phone_number_rows, invalid_email_rows)
        

    except ValueError as e:
        print(f"Error: {str(e)}")

if __name__ == '__main__':
    main()


    
