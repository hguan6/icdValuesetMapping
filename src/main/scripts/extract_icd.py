import pandas as pd




if __name__ == '__main__':
    valueset = pd.read_excel("../resources/dataset/valueset.xlsx",
                             sheet_name='Code List',
                             index_col=None,
                             header=10,
                             usecols=[0,2]
                             )
    icd_valueset = valueset.loc[valueset['Code System'] == 'ICD10CM']

    icd_valueset.to_csv("../resources/dataset/valueset.csv",columns=['Code'],header=False,index=False)

