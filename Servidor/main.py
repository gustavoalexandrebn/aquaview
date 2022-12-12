from flask import Flask, request, send_from_directory

from database_functions import insert_new_volume, get_clients_from_database, get_volumes_from_database, login_app
from extras_functions import get_time

app = Flask(__name__, template_folder='template')
DOWNLOAD_DIRECTORY = "imagens"


@app.route('/get-files/<path:path>', methods=['GET', 'POST'])
def get_files(path):
    """Download a file."""
    try:
        return send_from_directory(DOWNLOAD_DIRECTORY, path, as_attachment=True)
    except FileNotFoundError:
        print('ERRO')


@app.route('/login', methods=['POST'])
def login():
    cpf = request.form.get('cpf')
    senha = request.form.get('senha')

    return login_app(cpf, senha)


# Criar rotas entre o arduino e o servidor
@app.route('/get_clients', methods=['GET'])
def get_clientes():
    from json import dumps

    return dumps(get_clients_from_database())


# Criar rotas entre o arduino e o servidor
@app.route('/get_volumes', methods=['GET'])
def get_volumes():
    from json import dumps

    return dumps(get_volumes_from_database())


@app.route('/colete_volume_atual', methods=['POST'])
def endpoint_colete_volume_atual():
    from database_functions import colete_volume_atual

    return colete_volume_atual(request.form.get('cpf'))


@app.route('/colete_dado_diario', methods=['POST'])
def endpoint_colete_dado_diario():
    from database_functions import colete_dado_diario

    cpf = request.form.get('cpf')
    timestamp = request.form.get('timestamp')

    print(timestamp)
    return colete_dado_diario(cpf, timestamp)


@app.route('/colete_consumo_entre_datas', methods=['POST'])
def endpoint_colete_consumo_entre_datas():
    from database_functions import colete_consumo_entre_datas

    cpf = request.form.get('cpf')
    datainicial = request.form.get('datainicial')
    datafinal = request.form.get('datafinal')
    tipodedata = request.form.get('tipodedata')

    return colete_consumo_entre_datas(cpf, datainicial, datafinal, tipodedata)


@app.route('/colete_entrada_entre_datas', methods=['POST'])
def endpoint_colete_entrada_entre_datas():
    from database_functions import colete_entrada_entre_datas

    cpf = request.form.get('cpf')
    datainicial = request.form.get('datainicial')
    datafinal = request.form.get('datafinal')
    tipodedata = request.form.get('tipodedata')

    return colete_entrada_entre_datas(cpf, datainicial, datafinal, tipodedata)


@app.route('/get_volume_medium_from_user', methods=['POST'])
def get_volume_from_medium_from_user():
    from database_functions import get_medium_volume_value

    user = request.form.get('cpf')
    volume_value = get_medium_volume_value(user)

    dict = {'volume': volume_value}

    return dict


# Criar rotas entre o arduino e o servidor
@app.route('/post_volume', methods=['POST'])
def post_volume():
    volume = request.form.get('volume')
    id = request.form.get('id')
    time = get_time()

    insert_new_volume(time, volume, id)

    return "Volume insert"


# Criar rotas entre o arduino e o servidor
@app.route('/solicitar_agua', methods=['POST'])
def solicitar_agua():
    volumeemfalta = request.form.get('volumeemfalta')
    cpf = request.form.get('cpf')

    print(cpf + ' pediu ' + volumeemfalta + " litros de água!")

    return "Volume insert"


app.run(host='0.0.0.0', port=8080)
