<?php
/**
 * Created by PhpStorm.
 * User: mr-lvs
 * Date: 31/01/19
 * Time: 22:15
 */

class User extends CI_Controller
{

	public function __construct()
	{
		parent::__construct();
		$this->load->helper('url');
		$this->load->library('session');
		$this->load->model('Query');
		date_default_timezone_set('Asia/Jakarta');
		header('Access-Control-Allow-Origin: *');
		header("Access-Control-Allow-Methods: GET, POST, OPTIONS, PUT, DELETE");
		header('Content-Type: application/json');
	}

	public function index()
	{
		$u      = $this->security->xss_clean($this->input->post('username'));
		$p      = md5($this->security->xss_clean($this->input->post('password')));
		$query  = $this -> Query -> getData(array('username'=>$u,'password'=>$p, 'aktif'=>'1'),'groupuser') -> row();
		if($query == true) {
			$data['status'] = true;
			$data['msg'] = 'berhasil login';
			$data['nama'] = $query->nmlengkap;
			$data['username'] = $query->username;
			$data['email'] = $query->email;
			//mahasiswa 1, dosen 2
			$data['level'] = $query->id_level;
		}else{
			$data['status'] = false;
			$data['msg']	= 'Cek kembali username atau password anda';
			}
		echo json_encode($data);
	}


	public function cekVersionApp(){

		$cekVersion				= $this->input->post('version');

		$query  = $this -> Query -> getData(array('version'=>$cekVersion),'versionapp')-> row();

		if($query) {
			$data['status'] = true;
			$data['msg'] = 'OK';
			$data['versi'] 	= $query->version;
		}else{
			$data['status'] = false;
			$data['msg']	= 'Ada pembaruan aplikasi nih, silahkan klik ok untuk mendownload versi terbaru';
		}
		echo json_encode($data);
	}

}
