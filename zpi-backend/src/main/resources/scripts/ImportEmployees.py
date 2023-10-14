import pandas as pd
from datetime import datetime
import re

def capitalize_surname(surname):
    # Check if the input is NaN
    if pd.notna(surname):
        words = surname.split('-')
        words = [word.capitalize() for word in words]
        return '-'.join(words)
    else:
        return surname


def read_file(file_path):
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

        df["Tytuł/stopień"] = df['Tytuł/stopień'].str.lower()
        df['Nazwisko'] = df['Nazwisko'].apply(capitalize_surname)
        df['Imię'] = df['Imię'].str.capitalize()
        df['Jednostka'] = df['Jednostka'].str.upper()
        df['Podjednostka'] = df['Podjednostka'].str.upper()
        df['Stanowisko'] = df['Stanowisko'].str.lower()
        df['E-mail'] = df['E-mail'].str.lower()    


        wrong1 = df.loc[df['Lp.'] == 255, 'Telefon'].values[0]
        wrong2 = df.loc[df['Lp.'] == 320, 'Telefon'].values[0]
        wr1 = bool(re.match(r'^(?:\+\d{1,4}[\s-]?\d{2,4}[\s-]?\d{3}[\s-]?\d{3}|\d{2,4}[\s-]?\d{3}[\s-]?\d{3})?$', wrong1))
        wr2 = bool(re.match(r'^(?:\+\d{1,4}[\s-]?\d{2,4}[\s-]?\d{3}[\s-]?\d{3}|\d{2,4}[\s-]?\d{3}[\s-]?\d{3})?$', wrong2))

        #   picking invalid rows from the original dataframe through regex expressions
        invalid_index_rows = df[~df["Lp."].astype(str).str.match(r'^\d{1,5}$')]
        invalid_academic_title_rows = df[~df["Tytuł/stopień"].astype(str).str.match(r'^[a-z. ]{0,10}$')]
        invalid_surname_rows = df[~df["Nazwisko"].astype(str).str.match(r'^[A-Za-zĄĆĘŁŃÓŚŹŻąćęłńóśźż\s-]{0,50}$')]
        invalid_name_rows = df[~df["Imię"].astype(str).str.match(r'^[A-Za-zĄĆĘŁŃÓŚŹŻąćęłńóśźż\s-]{0,50}$')]
        invalid_unit_rows = df[~df["Jednostka"].astype(str).str.match(r'[A-Z0-9]{3,4}')]
        invalid_subunit_rows = df[~df["Podjednostka"].astype(str).str.match(r"^[A-Z0-9/]{1,10}$")]
        invalid_position_rows = df[~df["Stanowisko"].astype(str).str.match(r'^[a-z0-9zĄĆĘŁŃÓŚŹŻąćęłńóśźż\s()\-]{1,50}$')]


        invalid_phone_number_rows = df[~df["Telefon"].astype(str).str.match(r'^(?:\+\d{1,4}[\s-]?\d{2,4}[\s-]?\d{3}[\s-]?\d{3}|\d{2,4}[\s-]?\d{3}[\s-]?\d{3})?$')]


        invalid_email_rows = df[~df["E-mail"].astype(str).str.match(r'^[a-z0-9-]{1,50}(\.[a-z0-9-]{1,50}){0,4}@(?:student\.)?(pwr\.edu\.pl|pwr\.wroc\.pl)$')]

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

def main():
    file_path = "pracownicy-1.xlsx" 
    try:
        df_valid, invalid_index_rows, invalid_academic_title_rows,\
            invalid_surname_rows, invalid_name_rows, invalid_unit_rows,\
            invalid_subunit_rows, invalid_position_rows,\
            invalid_phone_number_rows, invalid_email_rows  = read_file(file_path)

        print ("\n- - - - - - - - - - - - - - -\nDataframe - valid\n")
        print(df_valid) 
        print ("\n- - - - - - - - - - - - - - -\nDataframe - invalid indexes\n")
        print(invalid_index_rows)
        print ("\n- - - - - - - - - - - - - - -\nDataframe - invalid academic titles\n")
        print(invalid_academic_title_rows)
        print ("\n- - - - - - - - - - - - - - -\nDataframe - invalid surnames\n")
        print(invalid_surname_rows)
        print ("\n- - - - - - - - - - - - - - -\nDataframe - invalid names\n")
        print(invalid_name_rows)
        print ("\n- - - - - - - - - - - - - - -\nDataframe - invalid units\n")
        print(invalid_unit_rows)
        print ("\n- - - - - - - - - - - - - - -\nDataframe - invalid subunits\n")
        print(invalid_subunit_rows)
        print ("\n- - - - - - - - - - - - - - -\nDataframe - invalid positions\n")
        print(invalid_position_rows)
        print ("\n- - - - - - - - - - - - - - -\nDataframe - invalid phone numbers\n")
        print(invalid_phone_number_rows)
        print ("\n- - - - - - - - - - - - - - -\nDataframe - invalid emails\n")
        print(invalid_email_rows)

    except ValueError as e:
        print(f"Error: {str(e)}")

if __name__ == '__main__':
    main()


    
